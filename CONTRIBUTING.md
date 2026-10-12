# Contributing

Contributions are welcome: bug reports, ideas and pull requests. This guide explains
how to set up the project and what we expect from a pull request.

## Before you start

- **Small fixes** (typos, translations, obvious bugs): open a pull request directly.
- **Bigger changes** (new features, reworks, anything touching networking or the
  script API): open an issue first so we can agree on the approach before you put
  time into it.
- **Security issues:** do not open a public issue. Report them privately via
  **Security → Report a vulnerability**; see [SECURITY.md](SECURITY.md).

## Setup

You need **JDK 17**. Everything else is handled by the Gradle wrapper.

```sh
./gradlew :assemble      # build the mod jar into build/libs/
./gradlew :check         # run the tests
./gradlew runClient      # start a dev client with the mod
./gradlew runServer      # start a dev server with the mod
```

The first build sets up the Minecraft 1.7.10 / Forge workspace and takes a few
minutes. If dependencies look stale, add `--refresh-dependencies`.

## Workflow

1. **Branch from `main`.** Members of the organization create a branch in this
   repository; everyone else forks the repository on GitHub and branches there.
2. **Name the branch** `<type>/<short-description>`, using the same types as commits
   (see below), for example:
   - `feat/finisher-camera`
   - `fix/privatevault-usability`
   - `docs/contributing-guide`
3. **Commit** using [Conventional Commits](#commit-messages).
4. **Open a pull request into `main`** and fill in what changed and why.
5. **Wait for review.** CI must pass and a maintainer has to approve before merging.

Never push directly to `main`: every push to `main` can publish a release.

## Commit messages

We use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/):

```
<type>(<optional scope>): <short summary in imperative mood>

<optional body: what and why, not how>

<optional footer, e.g. BREAKING CHANGE: ... or Closes #12>
```

| Type       | Use for                                                   |
|------------|-----------------------------------------------------------|
| `feat`     | A new feature or behaviour players or server owners see   |
| `fix`      | A bug fix                                                 |
| `refactor` | Code changes that neither fix a bug nor add a feature     |
| `perf`     | Performance improvements                                  |
| `test`     | Adding or fixing tests                                    |
| `docs`     | Documentation only (README, Javadoc, this file)           |
| `build`    | Gradle, dependencies, build scripts                       |
| `ci`       | GitHub Actions workflows                                  |
| `chore`    | Anything else that doesn't touch the mod's behaviour      |

Scopes are optional; use the area you changed, such as `gui`, `network`, `config`,
`api` or `lang`.

Examples:

```
feat(gui): add race stat editor
fix(network): ignore tab data requests from unknown players
chore(lang): add missing keys to de_DE
```

Mark breaking changes (for example, changes to the script API or anything that
forces clients to update) with `!` after the type, such as `feat(api)!: ...`, and
explain them in a `BREAKING CHANGE:` footer.

Keep each commit focused on one change. Clean up "wip" or "fix typo" commits before
asking for review, or we will squash them when merging.

## Pull request checklist

- [ ] `./gradlew :check` passes locally, and CI (**Build and test**, **CodeQL**) is green.
- [ ] The change is tested in game (`runClient` / `runServer`) if it affects gameplay
      or GUIs. Screenshots help for visual changes.
- [ ] New or changed text uses language keys. Add them to `en_US.lang` at least;
      other languages may fall back to English until translated.
- [ ] Changes to the public script API (`org.darkoro.zerosmod.api`) have Javadoc, since
      those docs are published.
- [ ] No version bumps (see below) unless a maintainer asked for one.
- [ ] No unrelated changes, generated files, or build output.

## Versions and releases

Maintainers handle versions and releases:

- `modVersion` in `gradle.properties` is the release version. When a push to `main`
  carries a new `modVersion`, CI builds, signs and publishes a GitHub release for it.
- `VERSION` in `ZeroSMod.java` is the client/server handshake version. Bump it only
  when clients must update.
- Server-only builds that keep compatibility with existing clients use a
  `-serverN` suffix on `modVersion`, for example `2.1.3-server4`.

## License of contributions

Zero S Mod is source-available under the [PolyForm Strict License 1.0.0](LICENSE.md),
not an open-source license. By submitting a contribution (code, assets, or other
changes) to this repository, you confirm that you have the right to do so, and you
grant the Jingames-Zeros organization a perpetual, worldwide, non-exclusive,
royalty-free, irrevocable license to use, modify, relicense and distribute your
contribution as part of Zero S Mod.
