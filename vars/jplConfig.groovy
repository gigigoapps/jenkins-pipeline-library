/**

  Global config variables

  Parameters:
  * String projectName
  * String targetPlatform
  * String jiraProjectKey
  * HashMap recipients

  ---------------
  cfg definitions
  ---------------
  * string  projectName             Project alias / codename (with no spaces)       (default: "project")
  * string  BRANCH_NAME             Branch name                                     (default: env.BRANCH_NAME)
  * string  laneName                Fastlane lane name                              (default: related to branch name)
  * string  targetPlatform          Target platform, one of these                   (default: "")
    - "android"
    - "ios"
    - "hybrid"
    - "backend"
  * boolean notify                  Automatically send notifications                (default: true)
  * string  archivePattern          Atifacts archive pattern
    Defaults
      - Android:  "** / *.apk"
      - iOS:      "** / *.ipa"
  * boolean promoteBuil             Flag to promote build to release steps          (default: false)

  * Hashmap applivery: Applivery parameters
        String token                Account api key                                 (default: jenkins env.APPLIVERY_TOKEN)
        String app                  App ID                                          (default: jenkins env.APPLIVERY_APP)
        String tags                 Tags                                            (default: '')
        boolean notify              Send notifications                              (default: true)
        boolean autotemove          Auto remove old builds                          (default: true)

  * Hashmap appetize: Appetize parameters
        String token                Token                                           (default: jenkins env.APPETIZE_TOKEN)
        String app                  App                                             (default: jenkins env.APPETIZE_APP)
  
  * Hashmap recipients: Recipients used in notifications
        String recipients.hipchat   List of hipchat rooms, comma separated          (default: "")
        String recipients.slack     List of slack channels, comma separated         (default: "")
        String recipients.email     List of email address, comma separated          (default: "")

  * HashMap sonar: Sonar scanner configuration
        String sonar.toolName                 => Tool name configured in Jenkins    (default: "SonarQube")
        String sonar.abortIfQualityGateFails  => Tool name configured in Jenkins    (default: true)

  * HashMap jira: JIRA configuration
        String jira.projectKey      JIRA project key                                (default: "")
        object jira.projectData     JIRA project data                               (default: "")

  * Hashmap ie: Integration Events configuration
        String ieCommitRawText      ie text as appears in commit message            (default: "" = no @ie command in the commit)
        String commandName          Command to be executed                          (default: "")
        Hashmap parameter           List of parameters and options                  (default: [:])
                                    Every parameter element of the hash contains:
                                    - String name: the string with the parameter
                                    - Hashmap option: List of options for the parameter.
                                    Every option of the hash contains:
                                    - String name: Name of the option
                                    - String status: "enabled" or "disabled", depending of the option status
  
  * Hashmap commitValidation: Commit message validation configuration on PR's, using project https://github.com/willsoto/validate-commit
        boolean enabled             Commit validation enabled status                (default: true)
        String preset               One of the willsoto validate commit presets     (default: 'eslint')
        int quantity                Number of commits to be checked                 (default: 10)
*/
def call (projectName = 'project', targetPlatform = '', jiraProjectKey = '', recipients = [hipchat:'',slack:'',email:'']) {
    cfg = [:]
    //
    if (env.BRANCH_NAME == null) {
        cfg.BRANCH_NAME = 'develop'
    }
    else {
        cfg.BRANCH_NAME = env.BRANCH_NAME
    }
    cfg.projectName                         = projectName
    cfg.laneName                            = ((cfg.BRANCH_NAME in ["staging", "qa", "quality", "master"]) || cfg.BRANCH_NAME.startsWith('release/')) ? cfg.BRANCH_NAME.tokenize("/")[0] : 'develop'
    cfg.versionSuffix                       = (cfg.BRANCH_NAME == "master") ? '' :  "rc" + env.BUILD_NUMBER + "-" + cfg.BRANCH_NAME.tokenize("/")[0]
    cfg.targetPlatform                      = targetPlatform
    switch (cfg.targetPlatform) {
        case 'android':
            cfg.archivePattern = '**/*.apk'
            break;
        case 'ios':
            cfg.archivePattern = '**/*.ipa'
            break;
        default:
            cfg.artifactsPattern = ''
            break;
    }
    cfg.promoteBuild = false

    //
    cfg.applivery = [:]
        cfg.applivery.token      = (env.APPLIVERY_TOKEN == null) ? '' : env.APPLIVERY_TOKEN
        cfg.applivery.app        = (env.APPLIVERY_APP   == null) ? '' : env.APPLIVERY_APP
        cfg.applivery.tags       = ''
        cfg.applivery.notify     = true
        cfg.applivery.autoremove = true

    //
    cfg.appetize = [:]
        cfg.appetize.token = (env.APPETIZE_TOKEN == null) ? '' : env.APPETIZE_TOKEN
        cfg.appetize.app   = (env.APPETIZE_APP   == null) ? '' : env.APPETIZE_APP
    
    //
    cfg.notify                              = true
    cfg.recipients                          = recipients

    //
    cfg.sonar = [:]
        cfg.sonar.toolName                  = "SonarQube"
        cfg.sonar.abortIfQualityGateFails   = true

    //
    cfg.jira = [:]
        cfg.jira.projectKey                 = jiraProjectKey
        cfg.jira.projectData                = ''

    //
    cfg.ie = [:]
        cfg.ie.ieCommitRawText              = ""
        cfg.ie.commandName                  = ""
        cfg.ie.parameter                    = [:]

    //
    cfg.commitValidation                    = [:]
        cfg.commitValidation.enabled        = true
        cfg.commitValidation.preset         = "eslint"
        cfg.commitValidation.quantity       = 10

    //-----------------------------------------//

    // Do some checks
    jplJIRA.checkProjectExists(cfg)

    // Return config HashMap
    return cfg
}
