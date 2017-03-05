; Copyright (c) meissa GmbH. All rights reserved.
; You must not remove this notice, or any other, from this software.

(ns  dda.pallet.crate.managed-ide.repos
  (:require
    [pallet.actions :as actions]
    [pallet.stevedore :as stevedore]
    [pallet.crate.git :as git]
    ))

(defn add-fingerprint-to-known-hosts
  "add a node qualified by ip or fqdn to the users ~/.ssh/known_hosts file."
  [os-user-name fingerprint]
  (let [command
         (str "echo \"" fingerprint "\" > ~/.ssh/known_hosts")]
     (pallet.action/with-action-options
          {:sudo-user os-user-name
           :script-env {:HOME (str "/home/" os-user-name)}}
          (actions/exec
            {:language :bash}
            (stevedore/script (~command))))))

(defn add-node-to-known-hosts
  "add a node qualified by ip or fqdn to the users ~/.ssh/known_hosts file."
  [os-user-name fqdn-or-ip & {:keys [port]                            ;; optional parameter
       :or {port 22}}]
  (let [home (str "/home/" os-user-name)
        command
         (str "ssh-keyscan -p " port " -H " fqdn-or-ip " >> " home "/.ssh/known_hosts")]
     (pallet.action/with-action-options
          {:sudo-user os-user-name
           :script-env {:HOME (str "/home/" os-user-name)}}
          (actions/exec
            {:language :bash}
            (stevedore/script (~command))))))

(defn clone-repos
  "clone repositories"
  [& {:keys [project-name
             project-parent-path
             custom-repo-name
             os-user-name
             git-user-name
             ssh-repo-urls]
      :or {project-destination "/code/"}}]
  (doseq [url ssh-repo-urls]
    (let [current-repo-path
          (str "/home/" os-user-name project-parent-path project-name "/"
               (if (nil? custom-repo-name)
                 (git/repo-name url)
                 custom-repo-name))]
      ; TODO - review mje 29.08.: move state management completly to pallet/state
      (actions/plan-when-not
        (stevedore/script (directory? current-repo-path))
        ;TODO: check if reachable
        (git/clone
          (if (or
                (.contains url "@")                  ;if username has already been specified ...
                (.contains url "http"))              ;or http is defined ...
            url                                    ; ... assume url to be correct or ...
            (str "ssh://" git-user-name "@" url))  ; ... else add git-user-name
          :checkout-dir current-repo-path))
      )))

(defn clone-project-repositories
  "clone repositories of one given project"
  [os-user-name git-user-name project-key project-repositories
   & {:keys [project-parent-path]
      :or {project-parent-path "/code/"}}]
  (clone-repos
    :project-name (name project-key)
    :os-user-name os-user-name
    :project-parent-path project-parent-path
    :git-user-name git-user-name
    :ssh-repo-urls project-repositories))

(defn clone-projects
  "clone all projects repositories to ~/code"
  [os-user-name git-user-name
   & {:keys [project-config]}]
  (doseq [[project-key project-repositories] project-config]
    (clone-project-repositories os-user-name git-user-name project-key project-repositories)))
