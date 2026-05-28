pipeline {

    agent any

    environment {

        DRIVERS_LOC = "$JENKINS_HOME/selenium-drivers"

    }

    tools {

        maven "maven default"

    }

    stages {

        stage('Git clone') {

            steps{

                git branch: 'main', url: 'https://github.com/ualhmis2026-hmis-lovers/sesion10.git'

            }

        }

        stage('Firefox tests') {

            steps {

                // Run Maven on xvfb environment display.

                // Update the path/to/your/pom.xml as necessary

                sh "xvfb-run mvn -f hmis-lovers/pom.xml clean test -Dwebdriver.gecko.driver=${DRIVERS_LOC}/geckodriver"

            }

            post {

                // If Maven was able to run the tests, even if some of the test

                // failed, record the test results

                success {

                    junit '**/target/surefire-reports/TEST-*.xml'

                }

            }

        }

        stage('Chrome tests') {

            steps {

                // Run Maven on xvfb environment display with Chrome driver.

                // Update the path/to/your/pom.xml as necessary

                sh "xvfb-run mvn -f hmis-lovers/pom.xml clean test -Dwebdriver.chrome.driver=${DRIVERS_LOC}/chromedriver"

            }

            post {

                // If Maven was able to run the tests, even if some of the test

                // failed, record the test results

                success {

                    junit '**/target/surefire-reports/TEST-*.xml'

                }

            }

        }

    }

}
