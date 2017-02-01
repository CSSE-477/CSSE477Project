package utils;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class FakeManifest extends Manifest {
	public String entryPoint;
	public String contextRoot;

	public FakeManifest(String entryPoint, String contextRoot) {
		this.entryPoint = entryPoint;
		this.contextRoot = contextRoot;
		
	}

	@Override
	public Attributes getMainAttributes() {
		Attributes a = new Attributes();
		a.putValue("Entry-Point", entryPoint);
		a.putValue("Context-Root", contextRoot);
		return a;
	}
}
