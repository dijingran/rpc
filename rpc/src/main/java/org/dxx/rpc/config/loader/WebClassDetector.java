package org.dxx.rpc.config.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 获取WEB-INF/classes和WEB-INF/lib/下，指定包的class
 * </p>
 * 
 * @author dixingxing
 * @date May 22, 2012
 */
public class WebClassDetector {

	private String webRoot;

	private String libPath;

	private String classesPath;

	static final Logger LOG = Logger.getLogger(WebClassDetector.class);
	/**
	 *seperator
	 */
	protected static final char SEP = '/';

	protected static final int CLASS_LEN = 6;

	protected static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

	private String[] excludePackages = new String[] {};

	/**
	 * 
	 * <p>只接受.class文件，并且排除内部类</p>
	 * 
	 * @author dixingxing	 
	 * @date May 23, 2012
	 */
	protected static class ClassFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			// 递归子目录
			if (pathname.isDirectory()) {
				return true;
			}
			if (!pathname.getName().endsWith(".class")) {
				return false;
			}
			if (pathname.getName().indexOf('$') > 0) {
				return false;
			}
			return true;
		}
	}

	private static boolean isClass(JarEntry je) {
		return je.getName().endsWith(".class");
	}

	/**
	 * 
	 * <p>按包名过滤</p>
	 *
	 * @param pkgs
	 * @param className
	 * @return
	 */
	protected boolean matchPackages(String[] pkgs, String className) {
		for (String e : excludePackages) {
			if (className.startsWith(e)) {
				return false;
			}
		}

		if (pkgs == null) {
			return true;
		}

		for (String pkg : pkgs) {
			if (className.startsWith(pkg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p></p>
	 *
	 * @param classes
	 * @param pkgs 用来判断是否是要查找的包
	 * @param jar
	 * @throws IOException
	 */
	protected void detectByJar(Set<Class<?>> classes, String[] pkgs, JarFile jar) throws IOException {
		Enumeration<JarEntry> enu = jar.entries();
		for (; enu.hasMoreElements();) {
			JarEntry je = enu.nextElement();
			if (!isClass(je)) {
				continue;
			}

			String name = je.getName().replace(SEP, '.');
			name = name.substring(0, name.length() - CLASS_LEN);

			if (!matchPackages(pkgs, name)) {
				continue;
			}

			if (name.indexOf('$') > 0) {
				continue;
			}

			try {
				classes.add(CLASS_LOADER.loadClass(name));
			} catch (Exception e) {
				LOG.warn(String.format("装载类:%s失败", name), e);
			}
		}
	}

	/**
	 * 
	 * @param webRootPath 
	 */
	public WebClassDetector(String webRootPath) {
		webRoot = webRootPath;
		libPath = webRoot + "WEB-INF" + SEP + "lib" + SEP;
		classesPath = webRoot + "WEB-INF" + SEP + "classes" + SEP;
	}

	/**
	* @param libPath
	* @param classesPath
	*/
	public WebClassDetector(String libPath, String classesPath) {
		super();
		this.libPath = libPath;
		this.classesPath = classesPath;
	}

	/**
	 * 
	 * <p>获取WEB-INF/classes和WEB-INF/lib/下，指定包的class</p>
	 *
	 * @param packages
	 * @return
	 */
	public Set<Class<?>> getClasses(String packages) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			classes.addAll(getClassesByFile(packages));
			classes.addAll(getClassesByJar(packages));
		} catch (IOException e) {
			LOG.error("加载class出错", e);
		}
		return classes;
	}

	private static class JarFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".jar");
		}
	}

	private String getLibPath() {
		return libPath;
	}

	private String getClassesPath() {
		return classesPath;
	}

	/**
	 * 
	 * <p>web/WEB-INF/lib下jar包中所有的class</p>
	 *
	 * @param packages
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("synthetic-access")
	private Set<Class<?>> getClassesByJar(String packages) throws IOException {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (packages == null) {
			return classes;
		}
		String[] pkgs = StringUtils.split(packages, ",");

		File f = new File(getLibPath());
		LOG.debug(String.format("lib目录为:%s", f.getPath()));
		File[] jars = f.listFiles(new JarFileFilter());

		if (jars == null) {
			return classes;
		}
		for (File jar : jars) {
			detectByJar(classes, pkgs, new JarFile(jar.getPath()));
		}
		return classes;
	}

	/**
	 * 
	 * <p>web/WEB-INF/classes下的所有class</p>
	 *
	 * @param packages 
	 * @return
	 * @throws IOException
	 */
	private Set<Class<?>> getClassesByFile(String packages) throws IOException {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (packages == null) {
			return classes;
		}

		String[] pkgs = StringUtils.split(packages, ",");

		for (String pkg : pkgs) {
			addClasses(classes, pkg);
		}
		return classes;
	}

	/**
	 * 
	 * <p>web/WEB-INF/classes下的所有class,递归package</p>
	 *
	 * @param classes
	 * @param pkg
	 * @throws IOException
	 */
	private void addClasses(Set<Class<?>> classes, String pkg) throws IOException {
		File f = new File(getClassesPath() + pkg.replace('.', SEP));

		File[] files = f.listFiles(new ClassFileFilter());
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				addClasses(classes, pkg + SEP + file.getName());
			} else {
				String name = pkg + '.' + file.getName().substring(0, file.getName().length() - CLASS_LEN);
				name = name.replace(SEP, '.');
				try {
					if (!matchPackages(null, name)) {
						continue;
					}
					classes.add(CLASS_LOADER.loadClass(name));
				} catch (Exception e) {
					LOG.warn(String.format("装载类:%s失败", name), e);
				}
			}
		}
	}

}
