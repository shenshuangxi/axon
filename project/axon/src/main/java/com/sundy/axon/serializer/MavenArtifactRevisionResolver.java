package com.sundy.axon.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.sundy.axon.common.io.IOUtils;

/**
 * 用于获取当前maven的版本号
 * @author Administrator
 *
 */
public class MavenArtifactRevisionResolver implements RevisionResolver {

	private final String version;
	
	public MavenArtifactRevisionResolver(String groupId, String artifactId) throws IOException {
        this(groupId, artifactId, MavenArtifactRevisionResolver.class.getClassLoader());
    }
	
	public MavenArtifactRevisionResolver(String groupId, String artifactId, ClassLoader classLoader)
            throws IOException {
        final InputStream propFile = classLoader.getResourceAsStream(
                "META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties");
        if (propFile != null) {
            try {
                Properties mavenProps = new Properties();
                mavenProps.load(propFile);
                version = mavenProps.getProperty("version");
            } finally {
                IOUtils.closeQuietly(propFile);
            }
        } else {
            version = null;
        }
    }
	
	public String revisionOf(Class<?> payloadType) {
        return version;
    }

}
