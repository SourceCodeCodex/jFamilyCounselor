<a name="readme-top"></a>

[![Java][java-shield]][java-url]
[![Scala][scala-shield]][scala-url]
[![Eclipse][eclipse-shield]][eclipse-url]
[![Release][release-shield]][release-url]
[![MIT License][license-shield]][license-url]


[java-shield]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white
[java-url]: https://www.java.com/en/

[scala-shield]: https://img.shields.io/badge/Scala-DC322F?style=for-the-badge&logo=scala&logoColor=white
[scala-url]: https://www.scala-lang.org/

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
    <img src="images/LRGLogo.png" alt="Logo">
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
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#xcore-summary">XCore summary</a></li>
        <li><a href="#discovering-the-winebar">Discovering the WineBar</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

![eclipse-img-url]

[eclipse-img-url]: images/Eclipse.png

**jFamilyCounselor** is an Eclipse plugin that runs a metric-based static code analysis, capable of identifying fragments containing hidden familial type correlations in Java source code. What hidden familial type correlations are and how jFamilyCounselor detects them are detailed in: Alin-Petru Roșu, Petru-Florin Mihancea, _Towards the Detection of Hidden Familial Type Correlations in Java Code_, LOOSE Research Group, 2022 (see the [docs](/docs/paper.pdf) directory).

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- GETTING STARTED -->
## Getting Started


### Requirements

* *Eclipse IDE* ([2022-9 M2](https://www.eclipse.org/downloads/packages/release/2022-09/m2) recommended)
* *JRE* >= 16

### Installation

1. Download [jFamilyCounselor.zip](jfamilycounselor.artifact/jFamilyCounselor.zip)
2. Open Eclipse and go go to `Help` > `Install New Software...`
3. Click on `Add` to add a new repository
4. Click on `Archive` and open the downloaded zip, then click `Add`
5. `jFamilyCounselor` should apper on the list. Select `jFamilyCounselor`
6. Continue the installation by clicking `Next`
7. Accept the license terms
8. Trust the usigned content of the unknown origin
9. Restart Eclipse


So as to validate whether the plugin was properly installed, we recommend you follow the listed steps:

- Download the dummy workapce from the `jfamilycounselor.example` directory and open it with Eclipse
- Import the project `WineBar` in Eclipse
- Right-click on the `WineBar` project in the `Package Explorer` view and select `Browse in Insider`. _Note: Make sure that the project is imported as a Java project, not as a plain one._
- Alternatively, right-rlick on the `WaiterTray` class (NOT on the compilation unit `WaiterTray.java`) in the `Package Explorer` view and select `Browse in Insider`. _Note: Browsing in Insider a compilation unit will result in no entries in the `Insider` view._
- In either scenarios, you should see an entry in the `Insider` view.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- USAGE -->
## Usage

_Note: It is very important that you at least skimmed through the <a href="./docs/paper.pdf">article</a> before reading this section._


### XCore summary

Working with/Extending  **jFamilyCounselor** requires the understading of some [XCore](https://github.com/SourceCodeCodex/XCore) concepts. XCore is a tool that dynamically generates the metamodel of static analyses. It works based on the following abstractions: `properties`, `groups` and `actions`. 

* A `property` represents a computation applied on an analysed element. For a class, the number of its fields, the number of its descendets, a boolean representing whether the class contains static methods or not, all represent properties of that class.
* A `group` models a one-to-many relationship between elements. Some possible groups of a class are: the group of fields or methods the class contains, the group of interfaces it implements, etc.
* An `action` triggers some behaviour that is related to that element. For instance, we can trigger a job that exports the results of a Java project analysis as a CSV file.

More on XCore can be found at https://github.com/SourceCodeCodex/XCore.

### Experimenting with the WineBar

The user interface of *jFamilyCounselor* is provided by the `Insider` plugin. To open an element in Insider you generally need to use the `Package Explorer` view. Upon right-clicking on a item from the Insider view, a menu appears, which contains all defined properties, groups and actions for that item.

![insider-img-url]

[insider-img-url]: images/Insider.png


Elements that are relevant to be browsed in Insider for jFamilyCounselor are:

- Java projects
- classes

The main characteristic of a Java project is its `MightHideFamilialCorrelationsClasses` group. This group identifies those classes that are susceptible to hide familial type correlations behind some references they declare. In order to export the results of the analysis aplied at a project's level, there are two actions of `ExportReport_<algorithm>`, each representing one of the two estimation methods presented in the <a href="./docs/paper.pdf">paper</a>. The CSV file will be placed in the output directory, in the `jFamilyCounselor-reports` folder.

![exports-url]

[exports-url]: images/Exports.png



For the analysis of a particular class, we make use of the defined properties and groups. We can calculate its `aperture coverage` using both discussed algorithms based on properties. For more details on how the values are obtained, we can examine its group of references pair - `MightHideFamilialCorrelationsRefPairs`. For each reference pair we can again compute its aperture coverage and look at its `possible/used types` groups.


![refs-url]

[refs-url]: images/Refs.png



<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what can make jFamilyCounselor more efective in detecting hidden familial type correlations. Any contributions you make are greatly appreciated.

If you have a suggestion, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b improvement/new-idea`)
3. Commit your Changes (`git commit -m 'Commiting the new idea'`)
4. Push to the Branch (`git push origin improvement/new-idea`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments


* [XCore](https://github.com/SourceCodeCodex/XCore)
* [Eclipse JDT](https://www.eclipse.org/jdt/)


<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

Alin-Petru Roșu - [rosualinpetru@gmail.com](mailto:rosualinpetru@gmail.com)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->
## License

All code is available to you under the MIT license, available at http://opensource.org/licenses/mit-license.php and also in the [LICENSE.md](LICENSE.md) file. 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

