package com.redpillanalytics.gradle

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
class ExtensionTest extends Specification {

   @ClassRule
   @Shared
   TemporaryFolder testProjectDir = new TemporaryFolder()

   @Shared
   File buildFile

   @Shared
   String taskName

   @Shared
   def result

   // run the Gradle build
   // return regular output
   def setupSpec() {

      buildFile = testProjectDir.newFile('build.gradle')
      buildFile << """
            plugins {
                id 'com.redpillanalytics.gradle-properties'
            }
        """
   }

   // helper method
   def executeSingleTask(String taskName, List otherArgs, Boolean logOutput = true) {
      otherArgs.add(0, taskName)
      log.warn "runner arguments: ${otherArgs.toString()}"
      // execute the Gradle test build
      result = GradleRunner.create()
              .withProjectDir(testProjectDir.root)
              .withArguments(otherArgs)
              .withPluginClasspath()
              .build()

      // log the results
      if (logOutput) log.warn result.getOutput()
      return result
   }

   def "Execute :properties task"() {
      given:
      taskName = 'properties'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      !result.tasks.collect { it.outcome }.contains('FAILURE')
      result.output.contains('pluginProps')
   }
}
