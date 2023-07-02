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
    Eclipse plugin bundling metric-based static code analyses that reveal fragments containing hidden type correlations
    <br />
    <a href="https://doi.org/10.1109/SCAM55253.2022.00022"><strong>« Approach</strong></a> |
    <a href="#"><strong>Tool Demo (Coming Soon) »</strong></a>
    <br />
    <hr />
  </p>
</div>

<!-- REQUIREMENT -->
# Requirements

**Minimal Requirements**
- *Java Version*: 17
- *Eclipse IDE for RCP and RAP Developers* [2023-06 R](https://www.eclipse.org/downloads/packages/release/2023-03/r/eclipse-ide-rcp-and-rap-developers) 

## Notes

- it is strongly recommended to **use the specified versions**
- the plugin does not currently work when installed on *Eclipse IDE for Java Developers*
- using a *Java >= 17* with any *Eclipse IDE for RCP and RAP Developers <= 2023-03 R* is impossible due to a bug in Eclipse, ultimately addressed [here](https://github.com/eclipse-jdt/eclipse.jdt.core/issues/962)

<!-- PLAIN INSTALLATION -->
# Plain Installation

1. In *Eclipse IDE for RCP and RAP Developers*, go to `Help` > `Install New Software...` > `Add...` > `Local...`
2. Open the `jfamilycounselor.repository` folder
3. Install the deployable feature `jFamilyCounselor` that should appear under the `LOOSE Research Group` category (check/uncheck the `Group items by category`)
4. Accept the license terms
5. Trust the unsigned content of the unknown origin
6. Restart Eclipse

## Check Installation

- Upon start, a log indicating that the plugin was loaded should be present in the `Error Log` view
- Create workspace and import the **Java** projects from `jfamilycounselor.example`
- Using the `Package Explorer` view, right-click on the `WineBar` project and select `Browse in Insider` (_Note: Make sure the projects have the Java nature_)
- Alternatively, using the `Package Explorer` view, right-click on the `WaiterTray` class (NOT on the compilation unit `WaiterTray.java`) and select `Browse in Insider` (_Note: A class is represented as a 'C' green encircled icon. Browsing in Insider a compilation unit will result in no entries in the `Insider` view_)
- In either scenarios, you should see an entry in the `Insider` view

## Installation Problems

If the starting log, or the `Browse in Insider` option is missing, or nothing can be seen upon browsing, the installation was not successful. Retry with a clean installation in a different, newly installed Eclipse.

<!-- DEVELOPMENT ENVIRONMENT SETUP -->
# Development Environment Setup

**jFamilyCounselor** is developed using the [XCore](https://github.com/SourceCodeCodex/XCore) framework. In order to install XCore, copy the [binaries](https://github.com/SourceCodeCodex/XCore/tree/master/latest) in the `dropins` folder of Eclipse. The binaries are also present in the `ro.lrg.insider/lib` folder.

Before working with **jFamilyCounselor**, it is recommended to experiment with XCore. Skimming through the [paper](https://doi.org/10.1109/SANER.2017.7884654) and following the XCore's tutorial (present on its repository Wiki) would be recommended. Also refer to the [XCore Training](#xcore-training) section.

## jFamilyCounselor's Workspace

In order to setup the  **jFamilyCounselor**'s workspace, configure it as specified in the [tutorial](#xcore-training) and import the following projects:
- `jfamilycounselor`
- `jfamilycounselor.test`
- `jfamilycounselor.feature`
- `ro.lrg.insider` (also found [here](https://github.com/SourceCodeCodex/XCore))

## Check Setup

- Use the launch configuration from the `jfamilycounselor` project to start an `Eclipse Application`
- Follow the [Check Installation](#check-installation) steps

For further explanations on how to use **jFamilyCounselor**, please refer to the tool demo paper (coming soon).

<!-- XCore Training -->
# XCore Training

Before working with **jFamilyCounselor**, it is recommended to experiment with XCore. Skimming through the [paper](https://doi.org/10.1109/SANER.2017.7884654) and following the XCore's tutorial (present on its [Wiki pages](https://github.com/SourceCodeCodex/XCore/wiki)) would be recommended. Also, refer to the dev notes below whenever stumbling upon a bug/problem.

## XCore Summary
XCore is a tool that dynamically generates the meta-model of static analyses. It works based on the following abstractions: `properties`, `groups` and `actions`. 

- a `property` represents a computation applied on an analysed element. For a class, the number of its fields, the number of its descendants, a boolean indicating whether the class contains static methods or not, all represent properties of that class
- a `group` models a one-to-many relationship between elements. Some possible groups of a class are: the group of fields or methods the class declares, the group of interfaces it implements, etc.
- an `action` triggers some behavior that is related to that element. For instance, we can trigger a job that exports the results of a Java project analysis as a CSV file

## XCore Tutorial Summary
- Make sure to have *Eclipse RCP Plug-in Developer Resources* installed
- Stop project automatic builds
- Create a new plugin project; add as *Imported Packages*: `ro.lrg.xcore.metametamodel` _(Note: The names of the projects that use XCore should not contain dots, otherwise there might be bugs)_
- Enable the XCore annotation processor (which should be visible only if XCore's jar was placed in `dropins`)
- Import the `ro.lrg.insider` plugin project and make the former project depend on it 
- Implement metrics _(Note: In order to properly see the XCore property page, make sure to be in the `Java` perspective, not in `Plugin Development` and go to the Java's projects properties through the `Package Explorer` view)_

<!-- Using jFamilyCounselor -->
# Using jFamilyCounselor - Going to the WineBar

**jFamilyCounselor** is an Eclipse plugin that runs a metric-based static code analysis, capable of identifying fragments containing hidden familial type correlations in Java source code.

_Note: Make sure the plugin is properly installed in order to continue._

## Export a WineBar Report

- In the `Package Explorer` view, right-click on the "WineBar" Java project > `Browse in Insider`
- In the `Insider` view, right-click on `WineBar` > `Actions` > `jfamilycounselor` > `AssignmentsBasedReport`.
- In the project's root folder there should be a directory containing the results: `jFamilyCounselor/reports` _(Note: same goes for exporing visualisations - HFTC View)_

## Analyse the `WaiterTray` class

- In the `Package Explorer` view, expand the project until reaching to the `WaiterTray.java` compilation unit.
- In the `Package Explorer` view, expand the compilation unit so as to see the `WaiterTray` class (green 'C'  icon) > right-click > `Browse in Insider`.
- In the `Insider` view, right-click on `ro.lrg.winebar.WaiterTray` > `Properties` > `jfamilycounselor` > `AssignmentsBasedApertureCoverage`.
- Right-click on `ro.lrg.winebar.WaiterTray` > `Groups` > `jfamilycounselor` > `RelevantReferencesPairs`.
- Right-click on any entry > `Properties` > `jfamilycounselor` > `AssignmentsBasedApertureCoverage`.
- Right-click on the entry for which the `aperture coverage` is minimum (0.5) > `Groups` > `jfamilycounselor` > `AssignmentsBasedUsedTypes`.

<!-- USAGE NOTES -->
# Notes on Using jFamilyCounselor

- The cast-based approach records a slower progress at the beginning, and becomes better afterwards owing to caching
- The implemented analyses perform heavy computations and use a lot of memory and CPU. It is recommended to allocate at least 6GB RAM for the Eclipse application that runs jFamilyCounselor in order to obtain results in a feasible amount of time
- The visualisation might not be displayed immediately after export due to some permission issues. It is best to configure Eclipse to use an external browser to fix this problem.
- XCore currently does not run any computation on separate threads and long operations can block the UI. It is best to analyse projects following these steps:
  - Export a report using one of the XCore actions. The export is located in the project's root folder
  - Use the results to determine the types that might be interesting to be analysed manually
  - Browse those types in Insider individually and navigate through its defined groups to see, for instance, its relevant references pairs, or possible/used types of a particular pair

<!-- DEVELOPER NOTES -->
# Notes on Developing jFamilyCounselor

- For better performance, it might be needed to reconfigure the `CacheSupervisor` depending on the available amount of RAM
- The `ro.lrg.jfamilycounselor.plugin` package contains all classes which are responsible for the metamodel's generation through XCore
- In terms of vocabulary, a `capability` is nothing but a glorified singleton that wraps some logic which can be tested in isolation and composed with other capabilities
- Always use the Java perspective, not the default Plugin Development; some XCore features function only when using the former

# Deployment

The deployment of the plugin is done using the `jfamilycounselor.feature`. The deployable features are exported in the `jfamilycounselor.repository` folder.

<!-- RESULTS & CORPUS -->
# Results. Corpus

## Initial Results

The project that was intensively analysed is `kettle-engine`. In order to reproduce **jFamilyCounselor**'s results presented in _Rosu et al., Towards the Detection of Hidden Familial Type Correlations in Java Code, SCAM 2022_, follow these steps:

- [Allocate more RAM](#allocate-more-ram)
- Download the source code of [Pentaho Kettle-Engine (1984dc13e773e7f12eb82e771a5ac8cdf86905e6)](https://github.com/pentaho/pentaho-kettle/tree/1984dc13e773e7f12eb82e771a5ac8cdf86905e6)
- Import as `Existing Maven projects`
- Browse `kettle-engine` in Insider
- Export reports with the desired approaches
- The reports can be found in the project's root folder

### Allocate more RAM

The plugin currently requires a lot of memory in order to perform the `Assignments Based` analysis on the target project. Therefore:

1. Go to the Eclipse's installation path
2. Search for the `eclipse.ini` file
3. Update the `-Xms` and `-Xmx` flags:
```
-Xms6g
-Xmx8g
```
## Other Projects

Other projects that were identified to contain hidden type correlations and which can help improving jFamilyCounselor are present in corpus used in the study: _Luis Mastrangelo, Matthias Hauswirth, and Nathaniel Nystrom. 2019. Casting about in the dark: an empirical study of cast operations in Java programs. Proc. ACM Program. Lang. 3, OOPSLA, Article 158 (October 2019), 31 pages, [DOI](https://doi.org/10.1145/3360584)._

<!-- LICENSE -->
# License

All code is available to you under the MIT license, available at http://opensource.org/licenses/mit-license.php and also in the [LICENSE.md](LICENSE.md) file

