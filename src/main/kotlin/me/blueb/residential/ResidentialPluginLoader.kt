package me.blueb.residential

import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository

@Suppress("UnstableApiUsage", "Unused")
class ResidentialPluginLoader : PluginLoader {
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()

        resolver.addDependency(Dependency(DefaultArtifact("org.postgresql:postgresql:42.7.7"), null))
        resolver.addDependency(Dependency(DefaultArtifact("com.mysql:mysql-connector-j:9.3.0"), null))
        resolver.addDependency(Dependency(DefaultArtifact("com.zaxxer:HikariCP:6.3.0"), null))

        resolver.addRepository(
            RemoteRepository.Builder(
                "paper",
                "default",
                "https://repo.papermc.io/repository/maven-public/"
            ).build()
        )
        classpathBuilder.addLibrary(resolver)
    }
}