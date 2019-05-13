package com.redpillanalytics.plugin

import com.redpillanalytics.plugin.containers.Namespace
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class TemplatePlugin implements Plugin<Project> {

   void apply(Project project) {

      // apply plugin for git properties
      project.apply plugin: "org.ajoberstar.grgit"
      project.apply plugin: "org.dvaske.gradle.git-build-info"

      project.configure(project) {
         extensions.create('template', TemplatePluginExtension)
      }

      project.afterEvaluate {
         project.template.extensions.namespaces = project.container(Namespace)
         //project.extensions.confluent.taskGroups.add(new Namespace(name: 'template'))

         // create git extensions
         project.ext.gitDescribeInfo = project.grgit?.describe(longDescr: true, tags: true)
         project.ext.gitLastTag = (project.ext.gitDescribeInfo?.split('-')?.getAt(0)) ?: 'v0.1.0'
         project.ext.gitLastVersion = project.ext.gitLastTag.replaceAll(/(^\w)/, '')
      }
      // end of afterEvaluate
   }
}

