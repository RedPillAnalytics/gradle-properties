package com.redpillanalytics.gradle.properties


import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class GradlePlugin implements Plugin<Project> {

   void apply(Project project) {

      project.configure(project) {
         extensions.create('pluginProps', GradleExtension)
      }
   }
}

