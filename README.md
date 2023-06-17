<a name="readme-top"></a>

[![Java][java-shield]][java-url]
[![Eclipse][eclipse-shield]][eclipse-url]
[![Release][release-shield]][release-url]
[![MIT License][license-shield]][license-url]

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.7027355.svg)](https://doi.org/10.5281/zenodo.7027355)


[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
[java-url]: https://www.java.com/en/

[eclipse-shield]: https://img.shields.io/badge/Eclipse-2C2255?style=for-the-badge&logo=eclipse&logoColor=white
[eclipse-url]: https://www.eclipse.org/

[release-shield]: https://img.shields.io/github/v/release/SourceCodeCodex/jFamilyCounselor?style=for-the-badge
[release-url]: https://github.com/SourceCodeCodex/jFamilyCounselor/releases

[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/SourceCodeCodex/jFamilyCounselor/blob/main/LICENSE.md


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="http://loose.cs.upt.ro/index.php">
    <img src="docs/images/LRGLogo.png" alt="Logo">
  </a>

  <h1 align="center">jFamilyCounselor</h1>

  <p align="center">
    Eclipse plugin that bundles metric-based static code analyses that reveal fragments containing hidden type correlations
    <br />
    <a href="docs/approach.pdf"><strong>« Approach</strong></a> |
    <a href="docs/tool.pdf"><strong>Tool Demo »</strong></a>
    <br />
    <hr />
  </p>
</div>

<!-- ENVIRONMENT SETUP -->
## Environment Setup

### Requirements

* *Eclipse IDE for RCP and RAP Developers* [2023-03 R](https://www.eclipse.org/downloads/packages/release/2023-03/r/eclipse-ide-rcp-and-rap-developers) 
* *Java version* 16

Notes:
- using a Java version > 16 with any Eclipse version <= 2023-03 R is impossible due to a bug that was addressed [here](https://github.com/eclipse-jdt/eclipse.jdt.core/issues/962).
- using 2023-06 R might have a bug as the build path appears as incomplete and XCore's binaries from the ``/lib`` directory need to be manually specified.

### Setup

jFamilyCounselor is developed using the [XCore](https://github.com/SourceCodeCodex/XCore) framework. In order to install XCore copy the [binaries](https://github.com/SourceCodeCodex/XCore/tree/master/latest) in the **dropins** folder of Eclipse. 

Before working with jFamilyCounselor, it is recommended to experiment with XCore. Skimming through the [paper](https://doi.org/10.1109/SANER.2017.7884654) that introduces XCore and following the tutorial specified in its documentation would be recommended. Also, refer to the dev notes below whenever stumbling upon a bug/problem.

Tutorial summary:
- have Eclipse RCP Plug-in Developer Resources installed
- stop automatic builds
- create a new plugin project and add and Imported Packaged: ``ro.lrg.xcore.metametamodel``
- the names of the projects that use XCore should not contain dots in it, otherwise there might be bugs
- enable the XCore annotation processor (which should be visible only if XCore's jar was placed in dropins)
- import the ``ro.lrg.insider`` plugin project and make the former project depend on it 
- implement metrics
- in order to properly see the XCore property page, go to the Java's projects properties through the Package Explorer View

In order to setup the development environment for jFamilyCounselor, create a new workspace and configure in the same way as the one from the tutorial, and import the following projects:
- ``jfamilycounselor``
- ``jfamilycounselor.test``
- ``jfamilycounselor.feature``
- ``ro.lrg.insider`` (found [here](https://github.com/SourceCodeCodex/XCore))

For further explanations on how to use jFamilyCounselor, please refer to the [Tool Demo](docs/tool.pdf).

### Developer Notes

#### Execution

- the implemented analyses perform heavy computations and require a lot of memory and CPU. It is recommended to allocate at least 6GB RAM for the process that run jFamilyCounselor.
- the cast-based approach records a slower progress at the beginning and better afterwards due to caching

#### Source code and vocabulary

- the ``ro.lrg.jfamilycounselor.plugin`` contains all classes that use XCore which generates the metamodel 
- a ``capability`` is nothing but a glorified singleton that wraps some logic which can be tested in isolation

<!-- TESTING & DEPLOYMENT -->
## Testing and deployment

jFamilyCounselor's test are located in the ``jfamilycounselor.test`` project.

The deployment of the plugin can be made using the ``jfamilycounselor.feature``.

<!-- LICENSE -->
## License

All code is available to you under the MIT license, available at http://opensource.org/licenses/mit-license.php and also in the [LICENSE.md](LICENSE.md) file

