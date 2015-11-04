(ns {{namespace}}.test-helpers
  (:require [datomic.api :as d]
            [io.pedestal.http :as http]
            [io.pedestal.test :refer [response-for]]
            [cheshire.core :as json]
            [{{namespace}}.service :as {{namespace}}]
            [{{namespace}}.db :as db]
            [{{namespace}}.util :as util]
            [{{namespace}}.bootstrap :as bootstrap]))

(def base-service-map (with-redefs [bootstrap/config-map (let [sys-map (util/edn-resource "system.edn")]
                                                            (assoc sys-map :datomic-uri (db/new-db-uri)))]
                        (require '[{{namespace}}.service :as {{namespace}}] :reload)
                        (require '[{{namespace}}.routes :as routes] :reload)
                        (assoc {{namespace}}/service
                               ::datomic-uri (bootstrap/conf :datomic-uri))))

(defn service-fn [serv-map]
  (::http/service-fn (http/create-servlet serv-map)))

(defn make-service
  ([]
   (make-service base-service-map))
  ([serv-map]
   (service-fn serv-map)))

(defn service
  "This generates a testable service for use with io.pedestal.test/response-for."
  ([]
   (service base-service-map))
  ([serv-map]
   (service serv-map (::datomic-uri serv-map
                                    (bootstrap/conf :datomic-uri))))
  ([serv-map datomic-uri]
   (db/bootstrap! datomic-uri)
   (make-service serv-map)))

(defn GET
  "Make a GET request on our service using response-for."
  [& args]
  (apply response-for (service) :get args))

(defn POST
  "Make a POST request on our service using response-for."
  [& args]
  (apply response-for (service) :post args))

(defn post-form
  ([URL-path payload-map]
   (post-form URL-path payload-map {}))
  ([URL-path payload-map opts]
   (let [[k v ] (first payload-map)
         sb ^StringBuilder (StringBuilder. (str k "=" v))]
     ;; Build up the Form args string
     (reduce (fn [form-sb [k v]]
               (.append form-sb "&" k "=" v))
             sb
             (rest payload-map))
     (response-for (service)
                 :post URL-path
                 :headers (merge {"Content-Type" "application/x-www-form-urlencoded"}
                                (:headers opts))
                 :body (.toString sb)))))

(defn post-json
  "Makes a POST request to URL-path expecting a payload to submit as JSON.

  Options:
  * :headers: Additional headers to send with the request."
  ([URL-path payload]
   (post-json URL-path payload {}))
  ([URL-path payload opts]
   (response-for (service)
                 :post URL-path
                 :headers (merge {"Content-Type" "application/json"}
                                 (:headers opts))
                 :body (util/write-json payload))))

(defn post-edn
  "Makes a POST request to URL-path expecting a payload to submit as edn.

  Options:
  * :headers: Additional headers to send with the request."
  ([URL-path payload]
   (post-edn URL-path payload {}))
  ([URL-path payload opts]
   (response-for (service)
                 :post URL-path
                 :headers (merge {"Content-Type" "application/edn"}
                                 (:headers opts))
                 :body (util/write-edn payload))))

(defn with-seeds
  "Return a db with seed tx-data applied"
  ([tx-data] (with-seeds (d/db (d/connect {{namespace}}/datomic-uri)) tx-data))
  ([db tx-data]
   (:db-after (d/with db tx-data))))

