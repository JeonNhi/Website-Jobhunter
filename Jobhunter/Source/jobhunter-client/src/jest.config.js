module.exports = {
    preset: 'ts-jest', // Sử dụng ts-jest để Jest có thể xử lý TypeScript
    testEnvironment: "jsdom",
    setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
    setupFilesAfterEnv: ["@testing-library/jest-dom"], // Đảm bảo Jest dùng jest-dom
    moduleNameMapper: {
      "^@/(.*)$": "<rootDir>/src/$1", // Ánh xạ @ đến src/
      "^components/(.*)$": "<rootDir>/src/components/$1", // Ánh xạ components đến src/components
      "^styles/(.*)$": "<rootDir>/src/styles/$1", // Ánh xạ styles đến src/styles
      "^config/(.*)$": "<rootDir>/src/config/$1", // Ánh xạ config đến src/config
      "^pages/(.*)$": "<rootDir>/src/pages/$1", // Ánh xạ pages đến src/pages
    },
    transform: {
      "^.+\\.(ts|tsx)$": "ts-jest", // Biên dịch TypeScript
    },
    moduleFileExtensions: ["js", "jsx", "ts", "tsx"], // Đảm bảo các phần mở rộng file được xử lý
    globals: {
      'ts-jest': {
        isolatedModules: true, // Giúp tăng tốc quá trình biên dịch
      },
    },
  };
  