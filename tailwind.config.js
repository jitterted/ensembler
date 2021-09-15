module.exports = {
    mode: 'jit',
    purge: [
        './src/main/resources/**/*.html',
    ],
    darkMode: false, // or 'media' or 'class'
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
    variants: {
        extend: {},
        backgroundColor: ['responsive', 'first', 'last', 'even', 'odd', 'hover', 'focus'],
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
