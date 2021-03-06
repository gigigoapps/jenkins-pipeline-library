/**

  Promote code on release

  Merge code from upstream branch to downstream branch, then make "push" to the repository

  The function uses "git promote" script of https://github.com/pedroamador/git-promote

  Parameters:
  * cfg jplConfig class object
  * String updateBranch The branch "source" of the merge
  * String downstreamBranch The branch "target" of the merge

*/
def call(cfg, String upstreamBranch, String downstreamBranch) {
    timestamps {
        ansiColor('xterm') {
          jplCheckoutSCM(cfg)
          sh "wget -O - https://raw.githubusercontent.com/pedroamador/git-promote/master/git-promote | bash -s -- -m 'Merge from ${upstreamBranch} with Jenkins' ${upstreamBranch} ${downstreamBranch}"
        }
    }
}
