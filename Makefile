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