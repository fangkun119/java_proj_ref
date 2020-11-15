import { GET_HOME_TIMELINE } from "../constants/actions";

const initState = {};

export default function reducer(state = initState, action) {
    const { statuses } = action.payload || {};
    switch (action.type) {
        case GET_HOME_TIMELINE:
            return {
                ...state,
                home: statuses,
            }
        default: 
            return state;
    }
} 
