package com.redpillanalytics.plugin

import com.redpillanalytics.common.GitUtils
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException

@Slf4j
class TemplatePluginExtension {

   GitUtils gitUtils

   def setParameters(Project project, String extension) {

      // Go look for any -P properties that have "extension." in them
      // If so... update the extension value
      project.ext.properties.each { key, value ->

         if (key =~ /$extension\./) {

            def (extensionName, property) = key.toString().split(/\./)

            if (extensionName == extension && project."$extension".hasProperty(property)) {

               log.warn "Setting configuration property for extension: $extension, property: $property, value: $value"

               if (project.extensions.getByName(extension)."$property" instanceof Boolean) {

                  project.extensions.getByName(extension)."$property" = value.toBoolean()
               } else if (project.extensions.getByName(extension)."$property" instanceof Integer) {

                  project.extensions.getByName(extension)."$property" = value.toInteger()
               } else {

                  project.extensions.getByName(extension)."$property" = value
               }
            }
         }
      }
   }

   String getBuildParameter(String varName) {
      return System.getenv(varName) ?: System.getenv('bamboo_' + varName)
   }

   def getDependency(Project project, String configuration, String regexp) {

      return project.configurations."$configuration".find { File file -> file.absolutePath =~ regexp }
   }

   boolean dependencyMatching(Project project, String configuration, String regexp) {
      return (project.configurations."$configuration".dependencies.find { it.name =~ regexp }) ?: false
   }

   boolean isUsableConfiguration(Project project, String configuration, String regexp) {

      try {

         if (getDependency(project, configuration, regexp)) {
            return true
         } else {
            return false
         }
      } catch (UnknownConfigurationException e) {
         return false
      }
   }

   String getParameter(Project project, String name, String extension, String defaultValue = null) {

      // define the value we get along the way
      String value

      String extName = "${extension}.${name}"
      if (project.ext.has(extName)) {

         // give precedence to Gradle properties passed with a dot (.) notation
         // For instance: checkmate.buildId instead of just buildId
         // this will hopefully allow me to gracefully remove the non-dot-notation properties over time

         value = project.ext.get(extName)

      } else if (project.ext.has(name)) {

         // next in order is a non dot-notation parameter
         // I would like to eventually phase this out

         value = project.ext.get(name)

      } else if (getBuildParameter(name)) {

         // next are non dot notation environment variables
         // note: we support the Bamboo weird way of doing variables
         value = getBuildParameter(name)
      } else {

         // next we return values from the custom extension
         value = project.extensions.getByName(extension).properties.get(name, defaultValue)

      }

      // we want to update the value in the extension in these cases
      // this way, listeners, other plugins, etc. can all use them
      if (project.extensions."$extension".hasProperty(name)) {

         project.extensions.getByName(extension)."$name" = value
      }

      // finally return it
      return value
   }

   String getBuildNumber() {
      return System.getenv('SOURCE_BUILD_NUMBER') ?: System.getenv('CF_BUILD_ID') ?: System.getenv('bamboo_buildNumber') ?: getTimestamp()
   }

   String getBuildNumExt() {
      return '.' + getBuildNumber()
   }

   String getBuildTag() {
      return System.getenv('BUILD_ID') ?: System.getenv('BUILD_TAG') ?: System.getenv('CF_BUILD_URL') ?: System.getenv('bamboo_buildResultKey') ?: getTimestamp()
   }

   String getBuildTagExt() {
      '-' + getBuildTag()
   }

   String getBuildUrl() {
      return System.getenv('BUILD_URL') ?: System.getenv('bamboo_resultsUrl')
   }


   String getCIServer() {
      if (System.getenv('JENKINS_HOME')) return 'jenkins'
      else if (System.getenv('CF_URL')) return 'codefresh'
      else if (System.getenv('bamboo_planKey')) return 'bamboo'
      else if (System.getenv('PROJECT_ID')) return 'cloud-build'
      else return 'other'
   }

   boolean isJenkins() {
      return getCIServer() == 'jenkins'
   }

   boolean isBamboo() {
      return getCIServer() == 'bamboo'
   }

   boolean isCloudBuild() {
      return getCIServer() == 'cloud-build'
   }

   boolean isCodeFresh() {
      return getCIServer() == 'codefresh'
   }

   String getRepositoryUrl() {
      return System.getenv('GIT_TAG') ?: System.getenv('CF_BUILD_URL') ?: System.getenv('bamboo_planRepository_repositoryUrl') ?: gitUtils.remoteUrl ?: ""
   }

   String getGitHubOrg() {
      getRepositoryUrl().find(/(\/|:)(.+)(\/)([^.]+)/) { all, firstSlash, org, secondSlash, repo ->
         return org.toString()
      }
   }

   String getGitHubRepo() {
      getRepositoryUrl().find(/(\/|:)(.+)(\/)([^.]+)/) { all, firstSlash, org, secondSlash, repo ->
         return repo.toString()
      }
   }

   String getBranch() {
      return System.getenv('GIT_LOCAL_BRANCH') ?: System.getenv('bamboo_planRepository_branchName') ?: gitUtils.initialBranch
   }

   String getCommitEmail() {
      return gitUtils.emailAddress
   }

   String getCommitHash() {
      return gitUtils.getCommitHash()
   }

   static generateBuildId() {
      return UUID.randomUUID().toString()
   }

   static getTimestamp() {
      return new Date().format('yyyy-MM-dd-HHmmssSS')
   }

}
