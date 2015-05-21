(set-env! :source-paths   #{"src"}
          :test-paths     #{"test"}
          :resource-paths #{"resources" "config"}
          :dependencies   '[[org.clojure/clojure "1.7.0-beta3"]
                            [org.clojure/core.async "0.1.346.0-17112a-alpha" :exclusions [[org.clojure/tools.analyzer.jvm]]]

                            [io.pedestal/pedestal.service "0.4.0" :exclusions [[org.slf4j/slf4j-api]
                                                                               [com.cognitect/transit-clj]]]
                            [io.pedestal/pedestal.jetty "0.4.0"]
                            [com.cognitect/transit-clj "0.8.271"]
                            [com.fasterxml.jackson.core/jackson-annotations "2.3.0"]

                            [ns-tracker "0.2.2"]
                            [environ "1.0.0"]

                            ;; Datomic
                            [com.datomic/datomic-free "0.9.5173" :exclusions [[joda-time]
                                                                              [org.slf4j/slf4j-nop]
                                                                              [org.slf4j/slf4j-log4j12]
                                                                              [com.fasterxml.jackson.core/jackson-annotations]]]
                            [io.rkn/conformity "0.3.4" :exclusions [com.datomic/datomic-free]]

                            ;; Logging
                            [org.slf4j/slf4j-api "1.7.12"]
                            [ch.qos.logback/logback-classic "1.1.3" :exclusions [[org.slf4j/slf4j-api]]]
                            ;; Chronicle Logger can't run on OSv - comment it out here and in logback.xml
                            [net.openhft/chronicle-logger-logback "1.1.0" :exclusions [[org.slf4j/slf4j-api]]]
                            [org.slf4j/jul-to-slf4j "1.7.12"]
                            [org.slf4j/jcl-over-slf4j "1.7.12"]
                            [org.slf4j/log4j-over-slf4j "1.7.12"]])

(def version "0.0.1-SNAPSHOT")
(task-options! pom {:project '{{name}}
                    :version (str version "-standalone")
                    :description "FIXME: write description"
                    :license {"License Name" "All Rights Reserved"}})

;; == Datomic =============================================
(load-data-readers!)

(deftask bootstrap
  "Bootstrap the Datomic database"
  []
  (require '[{{namespace}}.db :as db])
  ((resolve 'db/bootstrap!) @(resolve '{{namespace}}.db/uri)))

;; == Testing tasks ========================================

(deftask with-test
  "Add test to source paths"
  []
  (set-env! :source-paths #(clojure.set/union % (get-env :test-paths)))
  (set-env! :dependencies #(into % '[[criterium "0.4.3"]
                                     [thunknyc/profile "0.5.2"]
                                     [org.clojure/tools.trace "0.7.8"]
                                     [org.clojure/tools.namespace "0.2.10"]
                                     [org.clojure/test.check "0.8.0-ALPHA"]]))
  identity)

;; Include test/ in REPL sources
(replace-task!
  [r repl] (fn [& xs] (with-test) (apply r xs)))

(require '[clojure.test :refer [run-tests]])

(deftask test
  "Run project tests"
  []
  (with-test) ;; test source paths and test/dev deps added
  (bootstrap)
  (require '[clojure.tools.namespace.find :refer [find-namespaces-in-dir]])
  (let [find-namespaces-in-dir (resolve 'clojure.tools.namespace.find/find-namespaces-in-dir)
        test-nses              (->> (get-env :test-paths)
                                    (mapcat #(find-namespaces-in-dir (clojure.java.io/file %)))
                                    distinct)]
    (doall (map require test-nses))
    (apply clojure.test/run-tests test-nses)))

;; == Server Tasks =========================================

(deftask build
  "Build my project."
  []
  (comp (aot :namespace '#{ {{namespace}} })
        (pom)
        (uber)
        (jar :main '{{namespace}})))

(require '[{{namespace}} :as {{namespace}}])

(deftask server
  "Run a web server"
  []
  ({{namespace}}/start :io.pedestal.http/join? true))

