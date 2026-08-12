# MyBatis-Plus Reload

JRebel integration for MyBatis-Plus Mapper XML hot reload.

## Features

- Automatically integrates bundled `jr-mybatisplus` runtime with JRebel.
- Automatically injects `-Drebel.plugins` when launching with JRebel Run / Debug.
- Preserves existing `rebel.plugins` entries.
- Replaces older `jr-mybatisplus` runtime entries with the bundled version.
- No manual `-Drebel.plugins` configuration required.

## Compatibility

- IntelliJ IDEA 2026.2
- JRebel 2026.3.1 (real-project verification environment)
- MyBatis-Plus 3.5.2 (verified)
- MyBatis-Plus 3.5.7 (verified)

> Not every MyBatis-Plus 3.x version has been individually regression-tested.

## Runtime

Bundled runtime: `jr-mybatisplus` 1.0.8

Runtime repository: https://github.com/LayJustDoIt/jrebel-mybatisplus

## How it works

This plugin uses the `JavaProgramPatcher` extension point to intercept Java
run configurations launched via JRebel Run / JRebel Debug. Before the JVM
starts, it locates the bundled `jr-mybatisplus-1.0.8.jar` in the plugin's
`lib/` directory and injects `-Drebel.plugins=<path>` into the VM arguments.

If `-Drebel.plugins` already exists, the bundled runtime is merged using a
comma separator (per JRebel documentation). Old `jr-mybatisplus-*.jar` entries
are replaced; other plugin entries are preserved.

## Build

```shell
git clone https://github.com/LayJustDoIt/jrebel-mybatisplus-idea-plugin.git
cd jrebel-mybatisplus-idea-plugin
./gradlew buildPlugin
```

The plugin ZIP is generated at `build/distributions/jrebel-mybatisplus-idea-plugin-1.0.0.zip`.

## Credits

Original runtime: **SweetInk / suchu**

https://github.com/SweetInk/jrebel-mybatisplus

## License

MIT License — see [LICENSE](LICENSE).

Third-party notices: [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
