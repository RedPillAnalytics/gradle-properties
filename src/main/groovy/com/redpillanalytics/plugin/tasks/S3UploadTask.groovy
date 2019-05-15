package com.redpillanalytics.plugin.tasks

import com.amazonaws.services.s3.model.S3Object
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class S3UploadTask extends S3Task {

   S3UploadTask() {
      description = 'Upload a file from S3.'
      group = 'AWS'
   }

   @InputFile
   File getInputFile() {
      return file
   }

   @Internal
   S3Object getObject() {
      return s3.getObject(bucketName, keyName)
   }

   @TaskAction
   def s3Upload() {

      s3.putObject(bucketName, keyName, inputFile)

      //      try {
      //         outputFile.delete()
      //      } catch (Exception e) {
      //         log.warn "${e.message}"
      //      }
   }
}
