package com.redpillanalytics.common

class CI {

   static GitUtils gitUtils

   static String generateBuildId() {
      return UUID.randomUUID().toString()
   }

   static String getTimestamp() {
      return new Date().format('yyyy-MM-dd-HHmmssSS')
   }

   static String getBuildTag() {
      return System.getenv('BUILD_ID') ?: System.getenv('BUILD_TAG') ?: System.getenv('CF_BUILD_URL') ?: System.getenv('bamboo_buildResultKey') ?: getTimestamp()
   }

   static String getBuildParameter(String varName) {
      return System.getenv(varName) ?: System.getenv('bamboo_' + varName)
   }

   static String getBuildNumber() {
      return System.getenv('SOURCE_BUILD_NUMBER') ?: System.getenv('CF_BUILD_ID') ?: System.getenv('bamboo_buildNumber') ?: getTimestamp()
   }

   static String getBuildNumExt() {
      return '.' + getBuildNumber()
   }

   static String getBuildUrl() {
      return System.getenv('BUILD_URL') ?: System.getenv('bamboo_resultsUrl')
   }


   static String getCIServer() {
      if (System.getenv('JENKINS_HOME')) return 'jenkins'
      else if (System.getenv('CF_URL')) return 'codefresh'
      else if (System.getenv('bamboo_planKey')) return 'bamboo'
      else if (System.getenv('PROJECT_ID')) return 'cloud-build'
      else return 'other'
   }

   static boolean isJenkins() {
      return getCIServer() == 'jenkins'
   }

   static boolean isBamboo() {
      return getCIServer() == 'bamboo'
   }

   static boolean isCloudBuild() {
      return getCIServer() == 'cloud-build'
   }

   static boolean isCodeFresh() {
      return getCIServer() == 'codefresh'
   }

   static String getRepositoryUrl() {
      return System.getenv('GIT_TAG') ?: System.getenv('CF_BUILD_URL') ?: System.getenv('bamboo_planRepository_repositoryUrl') ?: gitUtils.remoteUrl ?: ""
   }

   static String getGitHubOrg() {
      getRepositoryUrl().find(/(\/|:)(.+)(\/)([^.]+)/) { all, firstSlash, org, secondSlash, repo ->
         return org.toString()
      }
   }

   static String getGitHubRepo() {
      getRepositoryUrl().find(/(\/|:)(.+)(\/)([^.]+)/) { all, firstSlash, org, secondSlash, repo ->
         return repo.toString()
      }
   }

   static String getBranch() {
      return System.getenv('GIT_LOCAL_BRANCH') ?: System.getenv('bamboo_planRepository_branchName') ?: gitUtils.initialBranch
   }

   static String getCommitEmail() {
      return gitUtils.emailAddress
   }

   static String getCommitHash() {
      return gitUtils.getCommitHash()
   }
}
