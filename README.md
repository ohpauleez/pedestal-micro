# pedestal-micro

A Leiningen template for building micro-services in Pedestal.

## Creating a project

```sh
$ lein new pedestal-micro com.example/web-service
```

The generated project includes both a
`build.boot` file (for [boot](http://boot-clj.com/), my preferred project manager) and,
`project.cl` (for [Leiningen](http://leiningen.org/)).

The two are roughly equivalent, but I suggest you pick one, and discard the
other.

### Tasks

| Task                   |     Boot      |   Leiningen    |
|------------------------|---------------|----------------|
| Launch a REPL          | `boot repl`   | `lein repl`    |
| Run Tests              | `boot test`   | `lein test`    |
| Launch a server        | `boot server` | `lein run`     |
| Build a deployable JAR | `boot build`  | `lein uberjar` |

## Config

The service is configured through a single file, `config/system.edn`.
If a requested config key is not present within that file, the system will
attempt to resolve it as an environment variable.
If there is no environment variable by that name, then a `not-found` value
will be used, which is `nil` by default.

You may want to update the `:host` value to `"0.0.0.0"` before deploying.

## Building a Docker container

```sh
# With Leiningen
$ lein uberjar

# With Boot
$ boot build

$ sudo docker build .
```

## Building and Running an OSv image

This requires [Capstan](https://github.com/cloudius-systems/capstan) to be installed.

```sh
# With Leiningen
$ lein uberjar

# With Boot
$ boot build

$ capstan run [any options you want]
```

You can also modify your `Capstanfile` to perform the build step for you.

Note that you will also have to remove the Chronicle dependency from your
`project.clj` or `boot.build` file, and comment/remove the Chronicle section
within `config/logback.xml`.

