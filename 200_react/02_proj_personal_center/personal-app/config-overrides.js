
// 导入组件的4种方法：
// `import { Menu } from 'antd'`            ：结合解构便捷导入，最常用最简单，但是需要搭配摇树优化插件才能实现按需加载
// `import Menu from 'antd/lib/menu'`       ：按需加载导入，只引入所需要的模块，减小打包体积
// `import { Menu } from 'antd/dist/antd'`  ：直接将整个包导入（导入了将打包好资源文件），所以也无法进行加载优化
// `const { Menu } = require('antd')`       ：nodejs的require导入方法，在 ES6 普及的当下，并不推荐在项目中使用这种写法（各类配置文件除外）
const { override, addLessLoader, fixBabelImports } = require('customize-cra'); 

// 依赖：
// npm install less-loader -D
// npm install less -D
// npm install babel-plugin-import -D
module.exports = override (
    fixBabelImports('antd', {
        libraryDirectory: 'es',
        style: 'css'
    }),
    addLessLoader({
        lessOptions : {
            javascriptEnabled: true,
        }
    })
)

