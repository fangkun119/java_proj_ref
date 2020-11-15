import React, { useEffect } from 'react';
import { useDispatch, useMappedState } from 'redux-react-hook';
import { getHomeTimeline } from '../../actions/timeline';

const Home = () => {
    const { 
        home = [] /*初始值为空，更新值从store中获取，其中会用到下面设置的函数来挑选与“信息流”有关的数据*/
    } =  useMappedState((state) => (state.timeline));

    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(getHomeTimeline())
    }, [dispatch]); //依赖[dispatch]：是一个规范，一旦dispatch发生变化，可以重新触发actions
    
    return(
        <div>
            {
                home.map(({id, text}) => (
                    <div key={id} style={{marginBottom : '10px'}}>
                        {text}
                    </div>
                ))
            }
        </div>
    );
};

export default Home;