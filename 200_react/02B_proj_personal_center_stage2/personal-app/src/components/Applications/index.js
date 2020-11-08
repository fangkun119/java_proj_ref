import React   from 'react';
import styles  from './index.module.less';
import numeral from 'numeral'; // npm install numeral -S
import { List, Card, Tooltip, Dropdown, Menu, Avatar } from 'antd';
import { DownloadOutlined, EditOutlined, ShareAltOutlined, EllipsisOutlined} from '@ant-design/icons'

const itemMenu = (
    <Menu>
        <Menu.Item><a target="_blank" rel="noopener noreferrer" href="https://www.alipay.com/">1st menu item</a></Menu.Item>
        <Menu.Item><a target="_blank" rel="noopener noreferrer" href="https://www.alipay.com/">2nd menu item</a></Menu.Item>
        <Menu.Item><a target="_blank" rel="noopener noreferrer" href="https://www.alipay.com/">3rd menu item</a></Menu.Item>
    </Menu>
)

const CardInfo = ({ activeUser, newUser }) => (
    <div className={styles.cardInfo}>
        <div>
            <p>活跃用户</p>
            <p>{activeUser}</p>
        </div>
        <div>
            <p>新增用户</p>
            <p>{newUser}</p>
        </div>        
    </div>
);

function formatWan(val) {
    const v = val * 1; //转成数字
    if (!v || Number.isNaN(v)) {
        return '';
    }
    let result = val;
    if (val > 10000) {
        result = (
            <span>
                {Math.floor(val / 10000)}
                <span>万</span>
            </span>
        )
    }
    return result;
}

const Applications = ({ list }) => {
    return (
        <List
            className={styles.filterCardList}
            rowKey="id" 
            grid={{ gutter:24, xxl:3, xl:2, lg:2, md:2, sm: 2, xs:1 }}
            dataSource={list}
            renderItem={(item) => (
                <List.Item key={item.id}>
                    <Card
                        hoverable={true}
                        bodyStyle={{ paddingBottom:20 }} 
                        actions={[
                            <Tooltip key="download" title="下载">
                                <DownloadOutlined />
                            </Tooltip>,
                            <Tooltip key="download" title="下载">
                                <EditOutlined />
                            </Tooltip>, 
                            <Tooltip key="share" title="分享">
                                <ShareAltOutlined />
                            </Tooltip>,
                            <Dropdown overlay={itemMenu} key="ellipsis">
                                <EllipsisOutlined />
                            </Dropdown>
                        ]}
                    >
                        <Card.Meta 
                            avatar={<Avatar size="small" src={item.avatar}/>} 
                            title={item.title} 
                        />
                        <div>
                            <CardInfo 
                                className={styles.cardInfo} 
                                activeUser={formatWan(item.activeUser)} 
                                newUser={numeral(item.newUser).format('0,0')}
                            />
                        </div>
                    </Card>
                </List.Item>
            )}
        />
    );
};

export default Applications;
