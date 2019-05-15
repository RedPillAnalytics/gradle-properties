package com.redpillanalytics.plugin

import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Slf4j
@Title("Execute :properties task")
class TasksTest extends Specification {

   @ClassRule
   @Shared
   TemporaryFolder testProjectDir = new TemporaryFolder()

   @Shared
   File buildFile
   @Shared
   def result

   // run the Gradle build
   // return regular output
   def setupSpec() {

      buildFile = testProjectDir.newFile('build.gradle')
      buildFile << """
            plugins {
                id 'com.redpillanalytics.plugin-template'
            }
        """

      result = GradleRunner.create()
              .withProjectDir(testProjectDir.root)
              .withArguments('tasks')
              .withPluginClasspath()
              .build()

      log.warn result.output
   }


   @Unroll
   def ":tasks contains #task"() {

      given: "executing Gradle :properties"

      expect:
      result.output.contains("$task")

      where:
      task << ['s3Upload','s3Download']
   }
}
