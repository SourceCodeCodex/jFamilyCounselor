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
    Eclipse plugin that detects hidden familial type correlations in Java code.
    <br />
    <a href="docs/paper.pdf"><strong>Paper »</strong></a>
    <br />
    <br />
    <a href="https://github.com/SourceCodeCodex/jFamilyCounselor/issues">Report Bug</a>
    ·
    <a href="https://github.com/SourceCodeCodex/jFamilyCounselor/issues">Propose Improvements</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#requirements">Requirements</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#development-environment">Development environment</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

![eclipse-img-url]

[eclipse-img-url]: docs/images/Eclipse.png

**jFamilyCounselor** is an Eclipse plugin that runs a metric-based static code analysis, capable of identifying fragments containing hidden familial type correlations in Java source code. Hidden familial type correlations have been introduced in: Alin-Petru Roșu, Petru-Florin Mihancea, _Towards the Detection of Hidden Familial Type Correlations in Java Code_, 22nd IEEE International Working Conference on Source Code Analysis and Manipulation, 2022.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- USING THE PLUGIN -->
## Using the plugin

_Note: It is very important that you at least skimmed through the <a href="./docs/paper.pdf">article</a> before reading this section._

### Requirements

* *Eclipse IDE* ([2023-03 R](https://www.eclipse.org/downloads/packages/release/2023-03/r) recommended)
* *JRE* = 16

_Note: For using any Java version newer than 16, please use any Eclipse IDE version newer than 2023-06. This is due to a bug that was addressed [here](https://github.com/eclipse-jdt/eclipse.jdt.core/pull/965)._

### Installing the binaries 

_Note: The steps are described relative to the Zenodo artifact._


1. Download `jFamilyCounselor.zip` from the `bin` directory
2. Open Eclipse and go to `Help` > `Install New Software...`
3. Click on `Add` to add a new repository
4. Click on `Archive` and open the downloaded zip, then click `Add`
5. `jFamilyCounselor` should have appeared on the list; select it
6. Click `Next`
7. Accept the license terms
8. Trust the unsigned content of the unknown origin
9. Restart Eclipse

### Check installation

So as to validate whether the plugin was properly installed, follow the listed steps:

- Download the dummy workspace from the `src/jfamilycounselor.example` directory and open it with Eclipse
- Import `WineBar` in Eclipse as a Java project
- Right-click on the `WineBar` project in the `Package Explorer` view and select `Browse in Insider`. _Note: Make sure that the project is imported as a Java project, not as a plain one._
- Alternatively, right-click on the `WaiterTray` class (NOT on the compilation unit `WaiterTray.java`) in the `Package Explorer` view and select `Browse in Insider`. _Note: A class is represented in `Package Explorer` as a 'C' encircled in a green circle icon. Browsing in Insider a compilation unit will result in no entries in the `Insider` view._
- In either scenarios, you should see an entry in the `Insider` view.


### XCore summary

Working with/Extending  **jFamilyCounselor** requires the understading of some [XCore](https://github.com/SourceCodeCodex/XCore) concepts. XCore is a tool that dynamically generates the metamodel of static analyses. It works based on the following abstractions: `properties`, `groups` and `actions`. 

* A `property` represents a computation applied on an analysed element. For a class, the number of its fields, the number of its descendants, a boolean representing whether the class contains static methods or not, all represent properties of that class.
* A `group` models a one-to-many relationship between elements. Some possible groups of a class are: the group of fields or methods the class contains, the group of interfaces it implements, etc.
* An `action` triggers some behaviour that is related to that element. For instance, we can trigger a job that exports the results of a Java project analysis as a CSV file.

More on XCore, including how to setup the environment, can be found at https://github.com/SourceCodeCodex/XCore.

### Experimenting with the WineBar

The user interface of *jFamilyCounselor* is provided by the `Insider` plugin. To open an element in Insider you generally need to use the `Package Explorer` view. Upon right-clicking on a item from the Insider view, a menu appears, which contains all defined properties, groups and actions for that item.

![insider-img-url]

[insider-img-url]: docs/images/Insider.png


Elements that are relevant to be browsed in Insider for jFamilyCounselor are:
- Java projects
- classes

The main characteristic of a Java project is its `MightHideFamilialCorrelationsClasses` group. This group identifies those classes that are susceptible to hide familial type correlations behind some references they declare. In order to export the results of the analysis aplied at a project's level, there are two actions of `ExportReport_<algorithm>`, each representing one of the two estimation methods presented in the <a href="./docs/paper.pdf">paper</a>. The CSV file will be placed in the output directory, in the `jFamilyCounselor-reports` folder.

![exports-url]

[exports-url]: docs/images/Exports.png



For the analysis of a particular class, we make use of the defined properties and groups. We can calculate its `aperture coverage` using both discussed algorithms based on properties. For more details on how the values are obtained, we can examine its group of references pair - `MightHideFamilialCorrelationsRefPairs`. For each reference pair we can again compute its aperture coverage and look at its `possible/used types` groups.


![refs-url]

[refs-url]: docs/images/Refs.png



<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Development environment
TO BE UPDATED


<!-- CONTRIBUTING -->
## Contributing

Contributions are what can make jFamilyCounselor more effective in detecting hidden familial type correlations. Any contributions you make are greatly appreciated.

<!-- ACKNOWLEDGMENTS -->
## Acknowledgments


* [XCore](https://github.com/SourceCodeCodex/XCore)
* [Eclipse JDT](https://www.eclipse.org/jdt/)

<!-- CONTACT -->
## Contact

Alin-Petru Roșu - [rosualinpetru@gmail.com](mailto:rosualinpetru@gmail.com)

<!-- LICENSE -->
## License

All code is available to you under [MIT license](http://opensource.org/licenses/mit-license.php).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

