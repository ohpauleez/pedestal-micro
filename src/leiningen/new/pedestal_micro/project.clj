(defproject {{raw-name}} "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "All Rights Reserved"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.2.371"]

                 [io.pedestal/pedestal.service "0.4.1"]
                 [io.pedestal/pedestal.jetty "0.4.1"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [com.fasterxml.jackson.core/jackson-annotations "2.3.2"]

                 [ns-tracker "0.2.2"]
                 [environ "1.0.0"]

                 ;; Datomic
                 [com.datomic/datomic-free "0.9.5327" :exclusions [[joda-time]
                                                                   [org.slf4j/slf4j-nop]
                                                                   [org.slf4j/slf4j-log4j12]
                                                                   [com.fasterxml.jackson.core/jackson-annotations]]]
                 [io.rkn/conformity "0.3.7" :exclusions [com.datomic/datomic-free]]

                 ;; Logging
                 [org.slf4j/slf4j-api "1.7.12"]
                 [ch.qos.logback/logback-classic "1.1.3" :exclusions [[org.slf4j/slf4j-api]]]
                 ;; Chronicle Logger can't run on OSv - comment it out here and in logback.xml
                 [net.openhft/chronicle-logger-logback "1.1.1" :exclusions [[org.slf4j/slf4j-api]]]
                 [org.slf4j/jul-to-slf4j "1.7.12"]
                 [org.slf4j/jcl-over-slf4j "1.7.12"]
                 [org.slf4j/log4j-over-slf4j "1.7.12"]]
  :min-lein-version "2.0.0"
  :resource-paths ["resources" "config"]
  :main ^{:skip-aot true} {{namespace}}.service
  ;:java-source-paths ["java"]
  ;:javac-options ["-target" "1.8" "-source" "1.8"]
  :global-vars  {*warn-on-reflection* true
                 *unchecked-math* :warn-on-boxed
                 ;*compiler-options* {:disable-locals-clearing true}
                 *assert* true}
  :pedantic? :abort
  :profiles {:uberjar {:aot [{{namespace}}.service]}
             :dev {:aliases {"dumbrepl" ["trampoline" "run" "-m" "clojure.main/main"]}
                   :dependencies [[criterium "0.4.3"]
                                  [thunknyc/profile "0.5.2"]
                                  [org.clojure/tools.trace "0.7.8"]
                                  [org.clojure/tools.namespace "0.2.10"]
                                  [org.clojure/test.check "0.8.0-alpha3"]]}}
  :plugins [[lein-marginalia "0.8.0" :exclusions [[org.clojure/clojure]
                                                  ;; Use the tools.reader from `cljfmt`
                                                  [org.clojure/tools.reader]]]
            [codox "0.8.12" :exclusions [[org.clojure/clojure]]]
            ;; Requires lein 2.5.0 or higher
            [lein-cljfmt "0.1.10" :exclusions [[org.clojure/clojure]]]]
  :jvm-opts ^:replace ["-d64" "-server"
                       "-Xms1g" ;"-Xmx1g"
                       "-XX:+UnlockCommercialFeatures" ;"-XX:+FlightRecorder"
                       ;"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8030"
                       "-XX:+UseG1GC"
                       ;"-XX:+UseConcMarkSweepGC" "-XX:+UseParNewGC" "-XX:+CMSParallelRemarkEnabled"
                       ;"-XX:+ExplicitGCInvokesConcurrent"
                       "-XX:+AggressiveOpts"
                       ;-XX:+UseLargePages
                       "-XX:+UseCompressedOops"])
