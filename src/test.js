const opts = {
    env: {
        print: console.log
    }
}

const execute = raw => {
    WebAssembly.instantiate(new Uint8Array(JSON.parse(raw)), opts)
        .then(it => it.instance.exports.run())
};

require('readline').createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false
}).on('line', execute)
