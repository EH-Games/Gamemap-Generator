package gamemap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {
	// set to true to only allow plugins and not other jars
	public static boolean restrictToPlugins = false;
	
	PluginClassLoader() {
		super(new URL[0], getSystemClassLoader());
		addPluginJars();
	}
	
	private void addPluginJars() {
		final File dir = new File("plugins");
		if(!dir.isDirectory()) return;
		
		final String pluginMeta = "/META-INF/services/" + Plugin.class.getName();
		
		for(File f : dir.listFiles()) {
			try {
				String filename = f.getName();
				URL url = f.toURI().toURL();
				String urlBase = url.toString();
				if(!f.isDirectory()) {
					if(!filename.toLowerCase().endsWith(".zip") && !filename.toLowerCase().endsWith(".jar")) continue;
					urlBase = "jar:" + urlBase + "!";
					filename = filename.substring(0, filename.length() - 4);
				}

				boolean add = !restrictToPlugins;
				if(!add) {
					URL svc_url = new URL(urlBase + pluginMeta);
					add = svc_url.getContent() != null;	
				}
				
				if(add) addURL(url);
				
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
