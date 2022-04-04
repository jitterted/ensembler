module.exports = {
    content: [
        './src/main/resources/static/index.html',
        './src/main/resources/templates/**/*.html',
    ],
    theme: {
        extend: {},
        minWidth: {
            '0': '0',
            '1/4': '25%',
            '1/2': '50%',
            '3/4': '75%',
            '4/5': '80%',
            'full': '100%',
        },
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
