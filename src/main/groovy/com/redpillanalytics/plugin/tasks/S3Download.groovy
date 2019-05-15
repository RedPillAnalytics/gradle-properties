package com.redpillanalytics.plugin.tasks

import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class S3Download extends S3Task {

   @OutputFile
   File getOutputFile() {
      return project.file(filePath)
   }

   @TaskAction
   def s3Download() {

      S3Object object = s3.getObject(bucketName, keyName)
      S3ObjectInputStream s3is = object.getObjectContent()
      outputFile.write(s3is)
   }
}
