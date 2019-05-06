package com.redpillanalytics.template
import com.redpillanalytics.common.GradleUtils
import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.testing.Test


@Slf4j
class TemplatePlugin implements Plugin<Project> {

   void apply(Project project) {

      // apply plugin for git properties
      project.apply plugin: "org.ajoberstar.grgit"
      project.apply plugin: "org.dvaske.gradle.git-build-info"

      project.afterEvaluate {
         // create git extensions
         project.ext.gitDescribeInfo = project.grgit?.describe(longDescr: true, tags: true)
         project.ext.gitLastTag = (project.ext.gitDescribeInfo?.split('-')?.getAt(0)) ?: 'v0.1.0'
         project.ext.gitLastVersion = project.ext.gitLastTag.replaceAll(/(^\w)/, '')
      }
      // end of afterEvaluate
   }
}

