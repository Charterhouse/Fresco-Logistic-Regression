Logistic Regression prototype using FRESCO
------------------------------------------

An early prototype for implementing [Logistic Regression][1] in the [FRESCO][2] framework. This uses [Secure Multi-Party Computation][3] to calculate a logistic regression on a combination of datasets from multiple parties, without disclosing the datasets between parties.

### Development setup:

1. Checkout this repository in a directory called `fresco-logistic-regression`.
2. Checkout out [our fork of FRESCO][4]. It has some small changes to FRESCO that haven’t landed in the main repo yet. Make sure that you check it out in a directory called `fresco` that is right next to `fresco-logistic-regression`.
3. Start [Intellij][5] and open the directory `fresco-logistic-regression`
4. When prompted, update Kotlin plugins. You need at least version 1.1 of Kotlin.
5. Check that Kotlin compiler is set to version 1.1. Go to `Preferences > Build, Execution, Deployment > Compiler > Kotlin Compiler` and make sure that both ‘Language version’ and ‘API version’ are set to 1.1.
6. When prompted, enable auto-update of the `fresco` maven projects.
7. when prompted, select a project SDK. You might need to tell Intellij where to find the Java SDK.

You should now be able to run the unit tests by right-clicking on `Fresco-Logistic-Regression > src > fresco` and select ‘Run Tests in fresco’

[1]: https://en.wikipedia.org/wiki/Logistic_regression
[2]: https://github.com/aicis/fresco
[3]: https://en.wikipedia.org/wiki/Secure_multi-party_computation
[4]: https://github.com/Charterhouse/fresco
[5]: https://www.jetbrains.com/idea/
