package com.redpillanalytics.common

import groovy.util.logging.Slf4j

@Slf4j

class Utils {

   /**
    * Return a list of File objects that match a particular extension.
    *
    * @return A list of File objects that match a particular extension.
    */
   static List getMatchingFiles(File sourceDir, String pattern) {

      return sourceDir.listFiles([accept: { file -> file ==~ /$pattern/ }] as FileFilter).toList()
   }

   /**
    * Return a list of File objects that match a particular extension.
    *
    * @return A list of File objects that match a particular extension.
    */
   static String getFileExt(String file) {
      // return a list of File objects that match a particular extension
      return file.tokenize('.').last()
   }

   /**
    * Return a list of File objects that match a particular extension.
    *
    * @return A list of File objects that match a particular extension.
    */
   static String getFileExt(File file) {
      return getFileExt(file.name)
   }

   /**
    * Return the base filename: filename without an extension.
    *
    * @return The base filename.
    */
   static String getFileBase(File file) {
      return file.name.tokenize('.').first()
   }

   /**
    * Return a list of files that match a particular extension.
    *
    * @return A list of files matching a particular extension.
    */
   static List getFilesByExt(File fileDir, String fileExt) {
      log.debug("fileDir: $fileDir")
      log.debug("fileExt: $fileExt")

      return fileDir.listFiles([accept: { file -> file ==~ /.*?\.$fileExt/ }] as FileFilter).toList().sort()
   }

   /**
    * Return a list of files that match a particular basename.
    *
    * @return A list of files matching a particular basename.
    */
   static List getFilesByBasename(File fileDir, String basename) {
      log.debug("fileDir: $fileDir")
      log.debug("basename: $basename")

      return fileDir.listFiles([accept: { file -> file ==~ /\.$basename\..*?/ }] as FileFilter).toList().sort()
   }

   /**
    * Return a file name with the new extension.
    *
    * @return A file name with the new extension.
    */
   static String getModifiedFileName(File file, String fileExt) {
      log.debug("original file: $file")
      log.debug("fileExt: $fileExt")

      // returns file with a different extension
      def returnFile = file.name.replaceFirst(~/\.[^\.]+$/, ".$fileExt")
      log.debug("file: $returnFile")

      return returnFile
   }

   /**
    * Return a modified file name where 'source' is replaced with 'target'.
    *
    * @return A modified file name where 'source' is replaced with 'target'.
    */
   static String getRenamedFileName(File file, String source, String target) {
      log.debug("original file: $file")

      // returns file with a different basename
      def fileName = file.name.replace(source, target)

      log.debug("file: $fileName")
      return fileName
   }

   /**
    * Return a file renamed where 'source' is replaced with 'target'.
    *
    * @return A file renamed where 'source' is replaced with 'target'.
    */
   static File getRenamedFile(File file, String source, String target) {
      return new File(file.parentFile, getRenamedFileName(file, source, target))
   }

   /**
    * Return a file with a new extension 'fileExt' and new parent directory 'fileDir'.
    *
    * @return A file with a new extension 'fileExt' and new parent directory 'fileDir'.
    */
   static File getModifiedFile(File file, File fileDir, String fileExt) {
      log.debug("fileDir: $fileDir")
      // returns a file with the same basename, but with a different path location, and different extension
      return new File(fileDir, getModifiedFileName(file, fileExt))
   }

   /**
    * Return a file with a new parent directory 'fileDir'.
    *
    * @return A file with a new parent directory 'fileDir'.
    */
   static File getModifiedFile(File file, File fileDir) {
      log.debug("file: $file")
      log.debug("fileDir: $fileDir")

      return new File(fileDir, file.name)
   }

   /**
    * Return a file with a new extension 'fileExt'.
    *
    * @return A file with a new extension 'fileExt'.
    */
   static File getModifiedFile(File file, String fileExt) {
      // returns a file with the same basename, same path, but different extension
      return new File(file.parent, getModifiedFileName(file, fileExt))
   }

   /**
    * Return a List of matching files in directory 'fileDir' that match extension 'currentExtension', but return them with new extension 'newExtension'.
    *
    * @return A List of matching files in directory 'fileDir' that match extension 'currentExtension', but return them with new extension 'newExtension'.
    */
   static List getModifiedFiles(File fileDir, String currentExtension, String newExtension) {
      return fileDir.listFiles([accept: { file -> file ==~ /.*?\.$currentExtension/ }] as FileFilter).toList().collect {
         file -> getModifiedFile(file, newExtension)
      }
   }

   /**
    * Return a List of files in directory 'fileDir' that match extension 'currentExtension', but return them with new parent directory 'newDir' and new extension 'newExtension'.
    *
    * @return A List of files in directory 'fileDir' that match extension 'currentExtension', but return them with new parent directory 'newDir' and new extension 'newExtension'.
    */
   static List getModifiedFiles(File currentDir, String currentExtension, File newDir, String newExtension) {
      def newFiles = currentDir.listFiles([accept: { file -> file ==~ /.*?\.$currentExtension/ }] as FileFilter).toList().collect {
         file -> getModifiedFile(file, newDir, newExtension)
      }
      return newFiles
   }

   /**
    * Return a List of matching files in directory 'fileDir' that match extension 'currentExtension', but return them with new parent directory 'newDir' and new extension 'newExtension'.
    *
    * @return A List of matching files in directory 'fileDir' that match extension 'currentExtension', but return them with new parent directory 'newDir' and new extension 'newExtension'.
    */
   static List getModifiedMatchingFiles(File currentDir, String currentExtension, File newDir, String newExtension, File matchDir, String matchExtension) {

      // On the left side of the intersect, I'm finding all files matching $currentExtension
      // I'm using the collect with getModifiedFile to get a file object with a new directory and new extension for each file
      // On the right side of the intersect, I'm testing to make sure these are actual files that exist.

      def newFiles = currentDir.listFiles([accept: { file -> file ==~ /.*?\.$currentExtension/ }] as FileFilter).toList().collect {
         file -> getModifiedFile(file, newDir, newExtension)
      }

      //intersect(matchDir.listFiles([accept: { file -> file ==~ /.*?\.$matchExtension/ }] as FileFilter).toList())
      return newFiles
   }

   static def exec(List command, File workingDir = null) {

      log.info("Utils Command: " + command.join(' '))
      log.info("Working Directory: $workingDir")

      def proc = command.execute(null, workingDir)

      proc.waitFor()
      log.info proc.in.text
      log.debug proc.err.text
      return proc
   }

   static void copy(File source, File target) {
      // copy the files
      target.bytes = source.bytes
      log.info "$source.canonicalPath file copied to $target.canonicalPath"
   }

   /**
    * Return a List of matching files in directory 'sourceDir' that match extension 'sourceExt'.
    *
    * @return A List of matching files in directory 'sourceDir' that match extension 'sourceExt'.
    */
   static List getMatchingFilesExt(File sourceDir, String sourceExt) {
      // return a list of File objects that match a particular extension
      return sourceDir.listFiles([accept: { file -> file ==~ /.*?\.$sourceExt/ }] as FileFilter).toList()
   }

   static void compareFiles(File baseFile, File compareFile) {
      // need to put in some logic to make sure the basename of the files are the same
      // raise an exception if they are not
      // the current logic here hasn't been tested
      assert baseFile.getName().replaceFirst(~/\.[^\.]+$/, null) == compareFile.getName().replaceFirst(~/\.[^\.]+$/, null)

      // compare "trimmed" versions of the files
      assert baseFile.text.trim() == compareFile.text.trim()
   }

   static String getToolExt() {
      return '.' + (System.getProperty("os.name").contains('Windows') ? 'cmd' : 'sh')
   }

   static String getModifiedBranch(String branchName) {
      if (!branchName) {
         return null
      } else if (CI.isJenkins() && !(branchName =~ /(.+)(\/)/)) {
         return getJenkinsRemote() + '/' + branchName
      } else {
         return branchName
      }
   }

   static String getJenkinsRemote() {
      if (!CI.isJenkins())
         return null
      else
         return System.getenv('GIT_BRANCH') - ~/\/.+/
   }

   static String getRelativePath(File root, File full) {
      return root.toURI().relativize(full.toURI()).toString()
   }

   static String getHostname() {
      return (new InetAddress().getLocalHost().getCanonicalHostName()) ?: 'localhost'
   }
}

