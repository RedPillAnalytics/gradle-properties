package com.redpillanalytics.gradle

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException

@Slf4j
class PropertiesPluginExtension {

   // will we support a replacement of "." with something else?
   Boolean enableReplacement = true

   // If we support replacement, what do we replace with?
   String replacementValue = '_'

   def setParameters(Project project, String extension) {

      project.ext.properties.each { key, value ->

         // first look for all properties that use a "." (period) to separate extensions
         // for instance, look for all "confluent" properties such as "confluent.pipelineEndpoint", so we can get "pipelineEndpoint".
         if (key =~ /$extension\./) {

            // split the property on the "." (period) to get "extensionName" and "property"
            def (extensionName, property) = key.toString().split(/\./)

            // if "extensionName" matches the one we are looking for, and "property" exists, then set that property in the extension
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

         // we also want to support using a period replacement value for environment variables
         // by default this is true, and uses an udnerscore
         if ( enableReplacement && key =~ /${extension}${replacementValue}/) {

            // first look for all properties that use a "." (period) to separate extensions
            // for instance, look for all "confluent" properties such as "confluent.pipelineEndpoint", so we can get "pipelineEndpoint".
            def (extensionName, property) = key.toString().split(/${replacementValue}/)

            // if "extensionName" matches the one we are looking for, and "property" exists, then set that property in the extension
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
