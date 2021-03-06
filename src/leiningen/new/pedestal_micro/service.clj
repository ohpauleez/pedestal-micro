(ns {{namespace}}.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ns-tracker.core :refer [ns-tracker]]
            [{{namespace}}.config :refer [config]]
            [{{namespace}}.util :as util]
            [{{namespace}}.routes :refer [routes]]
            [{{namespace}}.db :as db]
            [{{namespace}}.bootstrap :refer [conf]])
  (:gen-class))


(defonce modified-namespaces
  (if (conf :prod)
    (constantly nil)
    (ns-tracker ["src"])))

(defonce datomic-uri (conf :datomic-uri (db/new-db-uri)))

(def service
  {::http/host (or (conf :host) "127.0.0.1")
   ::http/port (Integer/parseInt (or (conf :port) "8080"))
   ::http/type :jetty
   ::http/join? false
   ::http/resource-path "/public"
   ::http/routes (if (conf :prod)
                   routes
                   (fn []
                     (doseq [ns-sym (modified-namespaces)]
                       (require ns-sym :reload))
                     (deref #'routes)))})

(defn server [service-overrides]
  (http/create-server (merge service
                             service-overrides)))

(defn start [& args]
  (db/bootstrap! datomic-uri)
  (let [service-overrides (apply hash-map args)
        server (server service-overrides)]
    (http/start server)))

(defn stop [serv]
  (if serv
    (http/stop serv)
    serv))

(defn restart [serv]
  (if serv
    (http/start (stop serv))
    serv))

(defn -main [& args]
  (start ::http/join? true
         ::http/routes routes))

