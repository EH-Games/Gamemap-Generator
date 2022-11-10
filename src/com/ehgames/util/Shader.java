package com.ehgames.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class Shader {
	private static boolean validShaderType(int type) {
		switch(type) {
			case GL.VERTEX_SHADER:
			case GL.FRAGMENT_SHADER:
			case GL.COMPUTE_SHADER:
			case GL.GEOMETRY_SHADER:
			case GL.TESS_CONTROL_SHADER:
			case GL.TESS_EVALUATION_SHADER:
				return true;
		}
		return false;
	}

	public final int	type;

	int					shader;

	private boolean		needsCompiled	= true;
	List<ShaderProgram>	attachedTo		= new ArrayList<>();
	private Path		sourceFile		= null;
	private FileTime	lastFileTime;
	String				currentSource;
	private String		lastCompilingSource;
	private boolean		errored = false;

	public Shader(int type) {
		if(!validShaderType(type)) {
			throw new IllegalArgumentException("Shader type is not valid");
		}

		this.type = type;
	}
	
	private void markCompilable() {
		needsCompiled = true;
		for(ShaderProgram program : attachedTo) {
			program.needsWork = true;
		}
	}
	
	public void revertToLastCompilingSource() {
		if(lastCompilingSource == null) {
			System.err.println("No compiling source exists for this shader");
			return;
		}
		
		currentSource = lastCompilingSource;
		markCompilable();
	}
	
	/**
	 * If the source for this shader was last specified using {@link #setSource(File)},
	 * checks to see if the file has been modified and reparses it if so
	 */
	public boolean detectFileChanges() {
		if(sourceFile == null) return false;
		if(!Files.isReadable(sourceFile)) return false;
		
		try {
			FileTime time = Files.getLastModifiedTime(sourceFile);
			if(!time.equals(lastFileTime)) {
				readFromSourceFile();
				return true;
			}
			errored = false;
		} catch(IOException e) {
			e.printStackTrace();
			errored = true;
		}
		return false;
	}
	
	public void setSource(String source) {
		currentSource = source;
		needsCompiled = true;
	}
	
	private void readFromSourceFile() {
		try {
			byte[] bytes = Files.readAllBytes(sourceFile);
			currentSource = new String(bytes, StandardCharsets.UTF_8);
			lastFileTime = Files.getLastModifiedTime(sourceFile);
			markCompilable();
		} catch(IOException e) {
			e.printStackTrace();
			errored = true;
		}
	}
	
	public void setSource(File f) {
		errored = false;
		sourceFile = f.toPath();
		readFromSourceFile();
	}
	
	public void setSource(URL url) {
		if(url == null) {
			throw new IllegalArgumentException("url can not be null");
		}
		sourceFile = null;
		try(InputStream stream = url.openStream()) {
			setSource(stream);
		} catch(IOException ioe) {
			ioe.printStackTrace();
			errored = true;
		}
	}
	
	public void setSource(InputStream stream) {
		errored = false;
		StringBuilder sb = new StringBuilder();
		boolean hasText = false;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			while(true) {
				String line = reader.readLine();
				if(line == null) break;
				
				if(hasText) sb.append('\n');
				sb.append(line);
				hasText = true;
			}
			currentSource = sb.toString();
			markCompilable();
		} catch(IOException ioe) {
			ioe.printStackTrace();
			errored = true;
		}
	}
	
	boolean attach(GL gl, ShaderProgram program) {
		boolean result = attach(gl, program.program);
		attachedTo.add(program);
		return result;
	}
	
	boolean attach(GL gl, int program) {
		if(shader == 0) {
			shader = gl.createShader(type);
		}
		
		// don't try compiling or attaching if we previously encountered an error.
		// this means that there's an error that previously occurred like
		// no source, unreadable source, non-compiling source, etc the user didn't fix 
		if(errored) return false;
		
		boolean ok = true;
		if(needsCompiled) {
			if(currentSource != null) {
				gl.shaderSource(shader, currentSource);
				gl.compileShader(shader);
				int compileStatus = gl.getShaderi(shader, GL.COMPILE_STATUS);
				String log = gl.getShaderInfoLog(shader);
				if(log != null && !log.isEmpty()) {
					System.err.println(log);
				}
				ok = compileStatus == GL.TRUE;
				if(ok) {
					lastCompilingSource = currentSource;
				}
			} else {
				System.err.println("Shader has no valid source attached");
				ok = false;
			}
			errored = !ok;
		}
		
		gl.attachShader(program, shader);
		return ok;
	}
	
	void destroy(GL gl) {
		if(shader != 0) {
			gl.deleteShader(shader);
			shader = 0;
		}
	}
}
