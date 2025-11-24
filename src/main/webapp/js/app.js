const { useState, useEffect } = React;

function App() {
    const [view, setView] = useState('login');
    const [user, setUser] = useState(null);

    const navigate = (newView) => setView(newView);

    return (
        <div className="app-container">
            {view === 'login' && <Login navigate={navigate} setUser={setUser} />}
            {view === 'register' && <Register navigate={navigate} />}
            {view === 'dashboard' && <UserDashboard user={user} navigate={navigate} />}
            {view === 'admin' && <AdminDashboard user={user} navigate={navigate} />}
        </div>
    );
}

function Login({ navigate, setUser }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async () => {
        try {
            const res = await fetch('api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if (res.ok) {
                setUser(data);
                if (data.role === 'ADMIN') {
                    navigate('admin');
                } else {
                    navigate('dashboard');
                }
            } else {
                setError(data.message);
            }
        } catch (e) {
            setError('Login failed');
        }
    };

    return (
        <div className="container" style={{ maxWidth: '400px', marginTop: '100px' }}>
            <div className="card" style={{ textAlign: 'center' }}>
                <h1 className="brand-title">FinTrack 360</h1>
                <h2>Login</h2>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
                <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                <button className="btn btn-primary" style={{ width: '100%' }} onClick={handleLogin}>Login</button>
                <p style={{ textAlign: 'center', marginTop: '10px' }}>
                    Don't have an account? <a href="#" onClick={(e) => { e.preventDefault(); navigate('register') }}>Register</a>
                </p>
            </div>
        </div>
    );
}

function Register({ navigate }) {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('USER');
    const [error, setError] = useState('');

    const handleRegister = async () => {
        try {
            const res = await fetch('api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password, role })
            });
            if (res.ok) {
                alert('Registration successful! Please login.');
                navigate('login');
            } else {
                const data = await res.json();
                setError(data.message);
            }
        } catch (e) {
            setError('Registration failed');
        }
    };

    return (
        <div className="container" style={{ maxWidth: '400px', marginTop: '100px' }}>
            <div className="card" style={{ textAlign: 'center' }}>
                <h1 className="brand-title">FinTrack 360</h1>
                <h2>Create Account</h2>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                <input type="text" placeholder="Full Name" value={name} onChange={e => setName(e.target.value)} />
                <input type="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
                <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                <select value={role} onChange={e => setRole(e.target.value)}>
                    <option value="USER">User</option>
                    <option value="ADMIN">Admin</option>
                </select>
                <button className="btn btn-primary" style={{ width: '100%' }} onClick={handleRegister}>Register</button>
                <p style={{ textAlign: 'center', marginTop: '10px' }}>
                    Already have an account? <a href="#" onClick={(e) => { e.preventDefault(); navigate('login') }}>Login</a>
                </p>
            </div>
        </div>
    );
}

function UserDashboard({ user, navigate }) {
    const [expenses, setExpenses] = useState([]);
    const [budgets, setBudgets] = useState([]);
    const [goals, setGoals] = useState([]);
    const [incomes, setIncomes] = useState([]);

    // Form States
    const [newExpense, setNewExpense] = useState({ amount: '', category: '', description: '', date: '' });
    const [newBudget, setNewBudget] = useState({ category: '', amount: '', startDate: '', endDate: '' });
    const [newGoal, setNewGoal] = useState({ name: '', targetAmount: '', deadline: '' });
    const [newIncome, setNewIncome] = useState({ amount: '', source: '', description: '', date: '' });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const expRes = await fetch('api/finance/expenses');
        setExpenses(await expRes.json());
        const budRes = await fetch('api/finance/budgets');
        setBudgets(await budRes.json());
        const goalRes = await fetch('api/finance/goals');
        setGoals(await goalRes.json());
        const incRes = await fetch('api/finance/incomes');
        if (incRes.ok) setIncomes(await incRes.json());
    };

    const addExpense = async () => {
        await fetch('api/finance/expenses', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newExpense)
        });
        setNewExpense({ amount: '', category: '', description: '', date: '' });
        fetchData();
    };

    const addBudget = async () => {
        await fetch('api/finance/budgets', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newBudget)
        });
        setNewBudget({ category: '', amount: '', startDate: '', endDate: '' });
        fetchData();
    };

    const addGoal = async () => {
        await fetch('api/finance/goals', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ...newGoal, currentAmount: 0, status: 'IN_PROGRESS' })
        });
        setNewGoal({ name: '', targetAmount: '', deadline: '' });
        fetchData();
    };

    const addIncome = async () => {
        await fetch('api/finance/incomes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newIncome)
        });
        setNewIncome({ amount: '', source: '', description: '', date: '' });
        fetchData();
    };

    const handleLogout = async () => {
        if (confirm('Are you sure you want to logout?')) {
            await fetch('api/auth/logout', { method: 'POST' });
            navigate('login');
        }
    };

    // Calculations
    const totalIncome = incomes.reduce((sum, inc) => sum + parseFloat(inc.amount), 0);
    const totalExpense = expenses.reduce((sum, exp) => sum + parseFloat(exp.amount), 0);
    const balance = totalIncome - totalExpense;

    return (
        <div className="container">
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <div>
                    <h1 className="brand-title" style={{ textAlign: 'left', fontSize: '2rem', marginBottom: '0' }}>FinTrack 360</h1>
                    <h3 style={{ marginTop: '5px' }}>Dashboard ({user ? user.name : ''})</h3>
                    <h3 style={{ color: balance >= 0 ? '#4caf50' : '#f44336' }}>
                        Balance: ${balance.toFixed(2)}
                    </h3>
                </div>
                <button className="btn" onClick={handleLogout}>Logout</button>
            </header>

            {/* Income & Expenses Section */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
                <div className="card">
                    <h3>Add Credit (Income)</h3>
                    <input type="number" placeholder="Amount" value={newIncome.amount} onChange={e => setNewIncome({ ...newIncome, amount: e.target.value })} />
                    <input type="text" placeholder="Source" value={newIncome.source} onChange={e => setNewIncome({ ...newIncome, source: e.target.value })} />
                    <input type="text" placeholder="Description" value={newIncome.description} onChange={e => setNewIncome({ ...newIncome, description: e.target.value })} />
                    <input type="date" value={newIncome.date} onChange={e => setNewIncome({ ...newIncome, date: e.target.value })} />
                    <button className="btn btn-primary" onClick={addIncome}>Add Credit</button>
                </div>

                <div className="card">
                    <h3>Add Expense</h3>
                    <input type="number" placeholder="Amount" value={newExpense.amount} onChange={e => setNewExpense({ ...newExpense, amount: e.target.value })} />
                    <input type="text" placeholder="Category" value={newExpense.category} onChange={e => setNewExpense({ ...newExpense, category: e.target.value })} />
                    <input type="text" placeholder="Description" value={newExpense.description} onChange={e => setNewExpense({ ...newExpense, description: e.target.value })} />
                    <input type="date" value={newExpense.date} onChange={e => setNewExpense({ ...newExpense, date: e.target.value })} />
                    <button className="btn btn-primary" style={{ backgroundColor: '#f44336' }} onClick={addExpense}>Add Expense</button>
                </div>
            </div>

            <div className="card">
                <h3>Recent Transactions</h3>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                    <div>
                        <h4>Incomes</h4>
                        <ul>
                            {incomes.map(inc => (
                                <li key={inc.id} style={{ color: '#4caf50' }}>
                                    +{inc.amount} ({inc.source}) <small>{inc.date}</small>
                                </li>
                            ))}
                        </ul>
                    </div>
                    <div>
                        <h4>Expenses</h4>
                        <ul>
                            {expenses.map(exp => (
                                <li key={exp.id} style={{ color: '#f44336' }}>
                                    -${exp.amount} ({exp.category}) <small>{exp.date}</small>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>

            {/* Budgets Section */}
            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3>My Budgets</h3>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '10px' }}>
                    <div>
                        <h4>Create Budget</h4>
                        <input type="text" placeholder="Category" value={newBudget.category} onChange={e => setNewBudget({ ...newBudget, category: e.target.value })} />
                        <input type="number" placeholder="Limit Amount" value={newBudget.amount} onChange={e => setNewBudget({ ...newBudget, amount: e.target.value })} />
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <input type="date" placeholder="Start Date" value={newBudget.startDate} onChange={e => setNewBudget({ ...newBudget, startDate: e.target.value })} />
                            <input type="date" placeholder="End Date" value={newBudget.endDate} onChange={e => setNewBudget({ ...newBudget, endDate: e.target.value })} />
                        </div>
                        <button className="btn btn-primary" onClick={addBudget}>Set Budget</button>
                    </div>
                    <div>
                        <h4>Active Budgets</h4>
                        <ul>
                            {budgets.map(b => (
                                <li key={b.id}>
                                    <strong>{b.category}</strong>: ${b.amount} <br />
                                    <small>{b.startDate} to {b.endDate}</small>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>

            {/* Goals Section */}
            <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h3>My Goals</h3>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                    <div>
                        <h4>Set New Goal</h4>
                        <input type="text" placeholder="Goal Name" value={newGoal.name} onChange={e => setNewGoal({ ...newGoal, name: e.target.value })} />
                        <input type="number" placeholder="Target Amount" value={newGoal.targetAmount} onChange={e => setNewGoal({ ...newGoal, targetAmount: e.target.value })} />
                        <input type="date" placeholder="Deadline" value={newGoal.deadline} onChange={e => setNewGoal({ ...newGoal, deadline: e.target.value })} />
                        <button className="btn btn-primary" onClick={addGoal}>Create Goal</button>
                    </div>
                    <div>
                        <h4>Active Goals</h4>
                        <ul>
                            {goals.map(g => (
                                <li key={g.id}>
                                    <strong>{g.name}</strong>: ${g.currentAmount} / ${g.targetAmount} <br />
                                    <small>Due: {g.deadline}</small>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    );
}

function AdminDashboard({ user, navigate }) {
    const [activeTab, setActiveTab] = useState('overview');
    const [logs, setLogs] = useState([]);
    const [users, setUsers] = useState([]);
    const [stats, setStats] = useState({ userCount: 0, expenseCount: 0, budgetCount: 0 });

    useEffect(() => {
        if (activeTab === 'overview') fetchStats();
        if (activeTab === 'users') fetchUsers();
        if (activeTab === 'logs') fetchLogs();
    }, [activeTab]);

    const fetchStats = async () => {
        const res = await fetch('api/admin/stats');
        if (res.ok) setStats(await res.json());
    };

    const fetchUsers = async () => {
        const res = await fetch('api/admin/users');
        if (res.ok) setUsers(await res.json());
    };

    const fetchLogs = async () => {
        const res = await fetch('api/admin/logs');
        if (res.ok) setLogs(await res.json());
    };

    const deleteUser = async (id) => {
        if (confirm('Are you sure you want to delete this user?')) {
            const res = await fetch(`api/admin/users?id=${id}`, { method: 'DELETE' });
            if (res.ok) fetchUsers();
            else alert('Failed to delete user');
        }
    };

    const handleLogout = async () => {
        if (confirm('Are you sure you want to logout?')) {
            await fetch('api/auth/logout', { method: 'POST' });
            navigate('login');
        }
    };

    return (
        <div className="container">
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <div>
                    <h1 className="brand-title" style={{ textAlign: 'left', fontSize: '2rem', marginBottom: '0' }}>FinTrack 360</h1>
                    <h3 style={{ marginTop: '5px' }}>Admin Console ({user ? user.name : ''})</h3>
                </div>
                <button className="btn" onClick={handleLogout}>Logout</button>
            </header>

            <div style={{ marginBottom: '20px' }}>
                <button className={`btn ${activeTab === 'overview' ? 'btn-primary' : ''}`} onClick={() => setActiveTab('overview')} style={{ marginRight: '10px' }}>Overview</button>
                <button className={`btn ${activeTab === 'users' ? 'btn-primary' : ''}`} onClick={() => setActiveTab('users')} style={{ marginRight: '10px' }}>Users</button>
                <button className={`btn ${activeTab === 'logs' ? 'btn-primary' : ''}`} onClick={() => setActiveTab('logs')}>Security Logs</button>
            </div>

            {activeTab === 'overview' && (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
                    <div className="card" style={{ textAlign: 'center' }}>
                        <h3>Total Users</h3>
                        <p style={{ fontSize: '2em', fontWeight: 'bold' }}>{stats.userCount}</p>
                    </div>
                    <div className="card" style={{ textAlign: 'center' }}>
                        <h3>Total Expenses</h3>
                        <p style={{ fontSize: '2em', fontWeight: 'bold' }}>{stats.expenseCount}</p>
                    </div>
                    <div className="card" style={{ textAlign: 'center' }}>
                        <h3>Total Budgets</h3>
                        <p style={{ fontSize: '2em', fontWeight: 'bold' }}>{stats.budgetCount}</p>
                    </div>
                </div>
            )}

            {activeTab === 'users' && (
                <div className="card">
                    <h3>User Management</h3>
                    <table style={{ width: '100%', textAlign: 'left' }}>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(u => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.name}</td>
                                    <td>{u.email}</td>
                                    <td>{u.role}</td>
                                    <td>
                                        {u.role !== 'ADMIN' && (
                                            <button className="btn" style={{ backgroundColor: '#f44336', padding: '5px 10px' }} onClick={() => deleteUser(u.id)}>Delete</button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === 'logs' && (
                <div className="card">
                    <h3>Security Logs</h3>
                    <table style={{ width: '100%', textAlign: 'left' }}>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Event</th>
                                <th>Description</th>
                                <th>IP</th>
                                <th>Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            {logs.map(log => (
                                <tr key={log.id}>
                                    <td>{log.id}</td>
                                    <td>{log.eventType}</td>
                                    <td>{log.description}</td>
                                    <td>{log.ipAddress}</td>
                                    <td>{log.timestamp}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
