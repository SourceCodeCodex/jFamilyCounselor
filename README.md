[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
[java-url]: https://www.java.com/en/

[eclipse-shield]: https://img.shields.io/badge/Eclipse-2C2255?style=for-the-badge&logo=eclipse&logoColor=white
[eclipse-url]: https://www.eclipse.org/

[release-shield]: https://img.shields.io/github/v/release/SourceCodeCodex/jFamilyCounselor?style=for-the-badge
[release-url]: https://github.com/SourceCodeCodex/jFamilyCounselor/releases

[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/SourceCodeCodex/jFamilyCounselor/blob/main/LICENSE.md

[![Java][java-shield]][java-url]
[![Eclipse][eclipse-shield]][eclipse-url]
[![Release][release-shield]][release-url]
[![MIT License][license-shield]][license-url]
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.7027355.svg)](https://doi.org/10.5281/zenodo.7027355)



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
    <a href="https://doi.org/10.1109/SCAM55253.2022.00022"><strong>« Approach</strong></a> |
    <a href="#"><strong>Tool Demo (Coming Soon) »</strong></a>
    <br />
    <hr />
  </p>
</div>

<!-- ENVIRONMENT SETUP -->
# Environment Setup

## Requirements

* *Eclipse IDE for RCP and RAP Developers* [2023-06 R](https://www.eclipse.org/downloads/packages/release/2023-03/r/eclipse-ide-rcp-and-rap-developers) 
* *Java version* 17

Notes:
- regardless of the chosen version, Eclipse IDE for **RCP and RAP Developers** is required both for using and working on the plugin
- using a Java version >= 17 with any Eclipse version <= 2023-03 R is impossible due to an Eclipse bug that was ultimately addressed [here](https://github.com/eclipse-jdt/eclipse.jdt.core/issues/962)

## Setup

jFamilyCounselor is developed using the [XCore](https://github.com/SourceCodeCodex/XCore) framework. In order to install XCore copy the [binaries](https://github.com/SourceCodeCodex/XCore/tree/master/latest) in the **dropins** folder of Eclipse. 

Before working with jFamilyCounselor, it is recommended to experiment with XCore. Skimming through the [paper](https://doi.org/10.1109/SANER.2017.7884654) that introduces XCore and following the tutorial specified in its documentation would be recommended. Also, refer to the dev notes below whenever stumbling upon a bug/problem.

Tutorial summary:
- have Eclipse RCP Plug-in Developer Resources installed
- stop automatic builds
- create a new plugin project and add and Imported Packaged: `ro.lrg.xcore.metametamodel`
- the names of the projects that use XCore should not contain dots in it, otherwise there might be bugs
- enable the XCore annotation processor (which should be visible only if XCore's jar was placed in dropins)
- import the `ro.lrg.insider` plugin project and make the former project depend on it 
- implement metrics
- in order to properly see the XCore property page, go to the Java's projects properties through the Package Explorer View

In order to setup the development environment of jFamilyCounselor, create a new workspace and configure in the same way as the one from the tutorial, and import the following projects:
- `jfamilycounselor`
- `jfamilycounselor.test`
- `jfamilycounselor.feature`
- `ro.lrg.insider` (found [here](https://github.com/SourceCodeCodex/XCore))

So as to validate that the setup was successful, follow the listed steps:

- start an Eclipse application and import the `jfamilycounselor.example/WineBar` project
- right-click on the `WineBar` project in the `Package Explorer` view and select `Browse in Insider`. _Note: Make sure that the project is imported as a Java project_
- alternatively, right-click on the `WaiterTray` class (NOT on the compilation unit `WaiterTray.java`) in the `Package Explorer` view and select `Browse in Insider`. _Note: A class is represented in `Package Explorer` as a 'C' encircled in a green circle icon. Browsing in Insider a compilation unit will result in no entries in the `Insider` view_
- in either scenarios, you should see an entry in the `Insider` view

For further explanations on how to use jFamilyCounselor, please refer to the tool demo (coming soon).

# Notes: Using jFamilyCounselor

- the cast-based approach records a slower progress at the beginning and better afterwards due to caching
- the implemented analyses perform heavy computations and use a lot of memory and CPU. It is recommended to allocate at least 6GB RAM for the Eclipse application that runs jFamilyCounselor in order to obtain results in a feasible amount of time
- for better performance, it might be needed to reconfigure the CacheSupervisor depending on the available amount of RAM
- the visualisation might not be displayed immediately after export due to some permission issues. It is best to configure Eclipse to use an external browser to fix this problem.
- XCore currently does not run any computation on separate threads and long operations can block the UI. It is best to analyze projects following these steps:
  - export a report using one of the XCore actions. The export is located in the project's root folder
  - use the results to determine the types that might be interesting to be analyzed manually
  - browse those types in Insider individually and navigate through its defined groups to see, for instance, its relevant references pairs, or possible/used types of a particular pair

# Developer Notes

## XCore Summary

Working with/Extending  **jFamilyCounselor** requires the understanding of some XCore concepts. XCore is a tool that dynamically generates the meta-model of static analyses. It works based on the following abstractions: `properties`, `groups` and `actions`. 

- a `property` represents a computation applied on an analysed element. For a class, the number of its fields, the number of its descendants, a boolean indicating whether the class contains static methods or not, all represent properties of that class
- a `group` models a one-to-many relationship between elements. Some possible groups of a class are: the group of fields or methods the class declares, the group of interfaces it implements, etc.
- an `action` triggers some behavior that is related to that element. For instance, we can trigger a job that exports the results of a Java project analysis as a CSV file

## Source Code. Vocabulary

- the `ro.lrg.jfamilycounselor.plugin` contains all classes which are responsible for the metamodel's generation through XCore
- a `capability` is nothing but a glorified singleton that wraps some logic which can be tested in isolation and composed with other capabilities
- always use the Java perspective, not the default Plugin Development. Some XCore features function only when using the former.

# Deployment

The deployment of the plugin can be made using the `jfamilycounselor.feature`. 
For now, the deployable feature (available inside `jfamilycounselor.repository`)
- open Eclipse and go to `Help` > `Install New Software...`
- add a new repository
- select `Archive` and open the exported zip
- select `jFamilyCounselor`
- accept the license terms
- trust the unsigned content of the unknown origin
- restart Eclipse

<!-- RESULTS & CORPUS -->
# Results. Corpus

So far, the project that was intensively analyzed is `kettle-engine`. In order to reproduce jFamilyCounselor's results, follow these steps:

- download the source code of [Pentaho Kettle-Engine](https://github.com/pentaho/pentaho-kettle/tree/1984dc13e773e7f12eb82e771a5ac8cdf86905e6)
- import as `Existing Maven projects`
- browse `kettle-engine` in Insider
- export reports with the desired approach
- the reports can be found in the project's root folder

Other projects that were identified to contain hidden type correlations and which can help improving jFamilyCounselor are present in corpus used in the study: Luis Mastrangelo, Matthias Hauswirth, and Nathaniel Nystrom. 2019. Casting about in the dark: an empirical study of cast operations in Java programs. Proc. ACM Program. Lang. 3, OOPSLA, Article 158 (October 2019), 31 pages, https://doi.org/10.1145/3360584.

<!-- LICENSE -->
# License

All code is available to you under the MIT license, available at http://opensource.org/licenses/mit-license.php and also in the [LICENSE.md](LICENSE.md) file

