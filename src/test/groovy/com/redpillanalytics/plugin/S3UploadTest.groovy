package com.redpillanalytics.plugin

import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Slf4j
@Stepwise
@Title("Execute ODI export tasks")
class S3UploadTest extends Specification {

   @Shared
   File projectDir, buildDir, buildFile, resourcesDir, settingsFile

   @Shared
   String taskName, odiPassword, masterUrl, masterPassword

   @Shared
   def result

   @Shared
   AntBuilder ant = new AntBuilder()

   def setup() {

      projectDir = new File("${System.getProperty("projectDir")}/download-test")
      buildDir = new File(projectDir, 'build')

      ant.mkdir(dir: projectDir)

      settingsFile = new File(projectDir, 'settings.gradle').write("""rootProject.name = 'download-test'""")

      buildFile = new File(projectDir, 'build.gradle').write("""
            |plugins {
            |    id 'com.redpillanalytics.plugin-template'
            |}
        |""".stripMargin())
   }

   // helper method
   def executeSingleTask(String taskName, List otherArgs, Boolean logOutput = true) {

      def args = [taskName] + otherArgs

      log.warn "runner arguments: ${args.toString()}"

      // execute the Gradle test build
      result = GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments(args)
              .withPluginClasspath()
              .build()

      // log the results
      if (logOutput) log.warn result.getOutput()

      return result
   }

   def "Execute :s3Download task with defaults"() {
      given:
      taskName = 's3Download'
      result = executeSingleTask(taskName, ['-Si', '--bucket-name', 'rpa-build-resources', '--key-name', 'gradle.properties'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }
}