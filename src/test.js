const magicModuleHeader = [0x00, 0x61, 0x73, 0x6d];
const moduleVersion = [0x01, 0x00, 0x00, 0x00];
const typeSection = [1, 8, 2, 96, 1, 125, 0, 96, 0, 0];
const importSection = [2, 27, 2, 3, 101, 110, 118, 5, 112, 114, 105, 110, 116, 0, 0, 3, 101, 110, 118, 6, 109, 101, 109, 111, 114, 121, 2, 0, 1];
const funcSection = [3, 2, 1, 1];
const exportSection = [7, 7, 1, 3, 114, 117, 110, 0, 1];

const code = [67, 0, 0, 96, 65, 16, 0]

const codeSection = [10, 11, 1, 9, 0, ...code, 11]

const wasm = new Uint8Array([
    ...magicModuleHeader,
    ...moduleVersion,
    ...typeSection,
    ...importSection,
    ...funcSection,
    ...exportSection,
    ...codeSection,
]);

const lengths = [magicModuleHeader.length, moduleVersion.length, typeSection.length, importSection.length, funcSection.length, exportSection.length, codeSection.length]
console.log(lengths);
console.log(lengths.reduce((acc, cur) => acc + cur));

const opts = {
    env: {
        print: console.log,
        memory: new WebAssembly.Memory({initial: 1})
    }
}

const run = it => it.instance.exports.run()
WebAssembly.instantiate(wasm, opts)
    .then(run)
    .then(console.log);
