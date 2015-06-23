(ns {{namespace}}.db
  "Datomic bootstrap"
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]))

(defn new-db-uri []
  (str "datomic:mem://" (d/squuid)))

(defn create-db [uri]
  ;; This is here in case we want to do any pre/post setup beyond Datomic
  (d/create-database uri))

(defn bootstrap!
  "Bootstrap schema into the database."
  [uri]
  (create-db uri)
  (let [conn (d/connect uri)]
    ;; TODO:     v Create resources/<your-schema.edn> and add "<your-schema>.edn" to this vector
    (doseq [rsc [ ]]
      (let [norms (c/load-schema-rsc rsc)]
        (c/ensure-conforms conn norms)))))

