# CSV Exporter

CSV Exporter is a demo web application to Convert [XES](http://xes-standard.org/) files to CSV.
Application is created for [Apromore](https://apromore.org/) as a new feature. 

## Running the Application
- Download the application or clone by executing `git clone https://github.com/ibrahimahdy/CSVExporter.git`.
- Navigate to `CSVExporter` directory.
- Build the application by running `mvn clean install`
- Start the application by running `mvn jetty:run`
- Navigate to [http://localhost:8080/apromore/](http://localhost:8080/apromore/index.zul)
- Add path to XES file from your system and click `Import XES file`

## Integeration into Apromore
Check code after it has been integerated into Apromore Code base as an OSGI plugin plugin.
- [CSVExporter-Portal](https://github.com/apromore/ApromoreCore/tree/master/Apromore-Custom-Plugins/CSVExporter-Portal)
- [CSVExporter-Logic](https://github.com/apromore/ApromoreCore/tree/master/Apromore-Custom-Plugins/CSVExporter-Logic)

## Technologies
### Java, Maven, Jetty, ZK, OSGI
------
