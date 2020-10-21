export DASM_LOG_ENABLED=false
./gradlew build && ./build/bin/macos/releaseExecutable/dasm.kexe | node ./src/test.js
