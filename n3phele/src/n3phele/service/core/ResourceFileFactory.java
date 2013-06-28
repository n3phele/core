package n3phele.service.core;

import java.io.FileNotFoundException;

public class ResourceFileFactory {
	
	public ResourceFile create(String filePath) throws FileNotFoundException
	{
		return new ResourceFile(filePath);
	}

}
