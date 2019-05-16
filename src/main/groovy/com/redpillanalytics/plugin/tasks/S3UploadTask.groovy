package com.redpillanalytics.plugin.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

@Slf4j
class S3UploadTask extends S3Task {

   S3UploadTask() {
      description = 'Upload a file to an S3 bucket.'
      group = 'AWS'
   }

   @InputFile
   File getInputFile() {
      File file = project.file(filePath)
      if (file.isDirectory()) throw new Exception("${file.relativePath()} is a directory.")
      return file
   }

   @TaskAction
   def s3Upload() {
      s3.putObject(bucketName, key, inputFile)
   }
}
