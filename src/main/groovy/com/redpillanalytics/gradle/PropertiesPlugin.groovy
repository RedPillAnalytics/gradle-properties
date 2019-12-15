package com.redpillanalytics.gradle


import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class PropertiesPlugin implements Plugin<Project> {

   void apply(Project project) {

      project.configure(project) {
         extensions.create('pluginProps', PropertiesPluginExtension)
      }
   }
}

