clean:
	./gradlew clean

install-dist:
	./gradlew installDist

run:
	./gradlew :distann:run

fatJar:
	./gradlew :distann:fatJar

installer:
	./gradlew installDist
	./gradlew -PENDING=pkg genset_build

run-fatJar:
	java --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED -cp distann/build/libs/distann-genset.jar org.simmi.distann.DistAnn