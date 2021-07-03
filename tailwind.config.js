module.exports = {
    mode: 'jit',
    purge: [
        './src/main/resources/**/*.html',
    ],
    darkMode: false, // or 'media' or 'class'
    theme: {
        extend: {},
    },
    variants: {
        extend: {},
    },
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
