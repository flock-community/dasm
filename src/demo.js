const opts = {
    env: {
        print: console.log, // Imported function
    }
}

const execute = bin => {
    WebAssembly.instantiate(bin, opts)
        .then(it => it.instance.exports.run()) // Exported function
}

execute(new Uint8Array([0, 97, 115, 109, 1, 0, 0, 0, 1, 8, 2, 96, 1, 125, 0, 96, 0, 0, 2, 13, 1, 3, 101, 110, 118, 5, 112, 114, 105, 110, 116, 0, 0, 3, 2, 1, 1, 7, 7, 1, 3, 114, 117, 110, 0, 1, 10, 24, 1, 22, 1, 1, 125, 67, 0, 0, 96, 65, 16, 0, 67, 0, 0, 64, 65, 33, 0, 32, 0, 16, 0, 11]))
