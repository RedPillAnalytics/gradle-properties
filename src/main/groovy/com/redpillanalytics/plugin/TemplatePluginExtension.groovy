package com.redpillanalytics.plugin

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException

@Slf4j
class TemplatePluginExtension {

   def setParameters(Project project, String extension) {

      // Go look for any -P properties that have "extension." in them
      // If so... update the extension value
      project.ext.properties.each { key, value ->

         if (key =~ /$extension\./) {

            def (extensionName, property) = key.toString().split(/\./)

            if (extensionName == extension && project."$extension".hasProperty(property)) {

               log.warn "Setting configuration property for extension: $extension, property: $property, value: $value"

               if (project.extensions.getByName(extension)."$property" instanceof Boolean) {

                  project.extensions.getByName(extension)."$property" = value.toBoolean()
               } else if (project.extensions.getByName(extension)."$property" instanceof Integer) {

                  project.extensions.getByName(extension)."$property" = value.toInteger()
               } else {

                  project.extensions.getByName(extension)."$property" = value
               }
            }
         }
      }
   }

   def getDependency(Project project, String configuration, String regexp) {

      return project.configurations."$configuration".find { File file -> file.absolutePath =~ regexp }
   }

   boolean dependencyMatching(Project project, String configuration, String regexp) {
      return (project.configurations."$configuration".dependencies.find { it.name =~ regexp }) ?: false
   }

   boolean isUsableConfiguration(Project project, String configuration, String regexp) {

      try {

         if (getDependency(project, configuration, regexp)) {
            return true
         } else {
            return false
         }
      } catch (UnknownConfigurationException e) {
         return false
      }
   }
}
